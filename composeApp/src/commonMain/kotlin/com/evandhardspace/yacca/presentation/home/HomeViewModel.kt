package com.evandhardspace.yacca.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.utils.formatToNDecimalPlaces
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val currencyRepository: CurrencyRepository,
    userRepository: UserRepository,
) : ViewModel() {

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
                state.copy(
                    currencyState = CurrencyState.CurrencyLoaded(
                        currencies.map {
                            CurrencyUi(
                                id = it.id,
                                name = it.name,
                                symbol = it.symbol,
                                price = "$${it.priceUsd.formatToNDecimalPlaces(3)}",
                                isFavourite = it.isFavourite,
                            )
                        }
                    )
                )
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
    }

    fun refresh() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(currencyState = CurrencyState.Loading)
            }
            currencyRepository.fetchCurrencies().fold(
                onSuccess = { /* todo */ },
                onFailure = { /* todo */ },
            )
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
            .fold(
                onSuccess = { /* todo */ },
                onFailure = { /* todo */ },
            )
    }

    private suspend fun deleteFromFavourites(currencyId: String) {
        currencyRepository.deleteFromFavourites(currencyId)
            .fold(
                onSuccess = { /* todo */ },
                onFailure = { /* todo */ },
            )
    }
}