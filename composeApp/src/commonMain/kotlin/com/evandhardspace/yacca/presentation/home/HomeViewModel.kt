package com.evandhardspace.yacca.presentation.home

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.models.Currency
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.presentation.AppEffect.*
import com.evandhardspace.yacca.presentation.SnackbarSendChannel
import com.evandhardspace.yacca.presentation.SnackbarState
import com.evandhardspace.yacca.utils.EffectViewModel
import com.evandhardspace.yacca.utils.NetworkMonitor
import com.evandhardspace.yacca.utils.formatToNDecimalPlaces
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val currencyRepository: CurrencyRepository,
    userRepository: UserRepository,
    private val snackbarChannel: SnackbarSendChannel,
    networkMonitor: NetworkMonitor,
) : EffectViewModel<HomeScreenEffect>() {

    private val _viewState = MutableStateFlow(
        HomeScreenState(
            currencyState = CurrencyState.Loading,
            isUserLoggedIn = false,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        currencyRepository.allCurrencies().onEach { currencies ->
            _viewState.update { state ->
                state.copy(currencyState = currencies.mapToCurrencyLoaded())
            }
        }.launchIn(viewModelScope)

        userRepository
            .isUserLoggedIn()
            .onEach { isUserLoggedIn ->
                _viewState.update { it.copy(isUserLoggedIn = isUserLoggedIn) }
                refresh()
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            currencyRepository.fetchCurrencies()
        }
        networkMonitor
            .isConnected
            .drop(1)
            .onEach { HomeScreenEffect.NetworkStateChanged(it).send() }
            .launchIn(viewModelScope)
    }

    fun sendSnackbar(effect: SnackbarEffect) {
        viewModelScope.launch {
            snackbarChannel.send(effect)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(currencyState = CurrencyState.Loading)
            }
            currencyRepository.fetchCurrencies()
                .onFailure { HomeScreenEffect.UnableToUpdate.send() }

            _viewState.update { state ->
                state.copy(
                    currencyState = currencyRepository
                        .allCurrencies()
                        .first()
                        .mapToCurrencyLoaded()
                )
            }
        }
    }

    fun onLike(currencyId: String) {
        viewModelScope.launch {
            val isFavourite = (_viewState.value.currencyState as? CurrencyState.CurrencyLoaded)
                ?.currencies
                ?.firstOrNull { it.id == currencyId }
                ?.isFavourite ?: false
            if (isFavourite) deleteFromFavourites(currencyId)
            else addToFavourite(currencyId)

        }
    }

    private suspend fun addToFavourite(currencyId: String) {
        currencyRepository.addToFavourites(currencyId)
            .onFailure { HomeScreenEffect.UnableToAdd.send() }
    }

    private suspend fun deleteFromFavourites(currencyId: String) {
        currencyRepository.deleteFromFavourites(currencyId)
            .onFailure { HomeScreenEffect.UnableToDelete.send() }
    }
}

private fun List<Currency>.mapToCurrencyLoaded(): CurrencyState.CurrencyLoaded =
    CurrencyState.CurrencyLoaded(
        currencies = map {
            CurrencyUi(
                id = it.id,
                name = it.name,
                symbol = it.symbol,
                price = "$${it.priceUsd.formatToNDecimalPlaces(3)}",
                isFavourite = it.isFavourite,
            )
        }
    )