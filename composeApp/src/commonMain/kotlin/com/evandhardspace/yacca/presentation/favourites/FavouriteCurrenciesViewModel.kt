package com.evandhardspace.yacca.presentation.favourites

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.domain.models.Currency
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.presentation.AppEffect
import com.evandhardspace.yacca.presentation.SnackbarSendChannel
import com.evandhardspace.yacca.presentation.home.CurrencyState
import com.evandhardspace.yacca.presentation.home.CurrencyUi
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import com.evandhardspace.yacca.utils.formatToNDecimalPlaces
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class FavouriteCurrenciesViewModel(
    private val cleanUpManager: CleanUpManager,
    private val currencyRepository: CurrencyRepository,
    private val snackbarSendChannel: SnackbarSendChannel,
) : EffectViewModel<FavouriteCurrenciesEffect>() {

    private val _viewState = MutableStateFlow<CurrencyState>(CurrencyState.Loading)
    val viewState = _viewState.asStateFlow()

    init {
        currencyRepository.favouriteCurrencies()
            .onEach { currencies ->
                _viewState.update { currencies.mapToCurrencyLoaded() }
            }.launchIn(viewModelScope)
        viewModelScope.launch {
            currencyRepository.fetchCurrencies()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _viewState.update { CurrencyState.Loading }

            currencyRepository.fetchCurrencies()
                .onFailure { FavouriteCurrenciesEffect.UnableToUpdate.send() }

            _viewState.update {
                currencyRepository
                    .favouriteCurrencies()
                    .first()
                    .mapToCurrencyLoaded()
            }
        }
    }

    fun deleteFromFavourites(currencyId: String) {
        viewModelScope.launch {
            currencyRepository.deleteFromFavourites(currencyId)
                .onFailure { FavouriteCurrenciesEffect.UnableToDelete.send() }
        }
    }

    fun sendSnackbar(effect: AppEffect.SnackbarEffect) {
        viewModelScope.launch {
            snackbarSendChannel.send(effect)
        }
    }

    fun logout() {
        viewModelScope.launch {
            cleanUpManager.clear()
            _viewState.update { CurrencyState.Loading }
            FavouriteCurrenciesEffect.LoggedOut.send()
        }
    }
}

internal sealed interface FavouriteCurrenciesEffect : Effect {
    data object LoggedOut : FavouriteCurrenciesEffect
    data object UnableToUpdate : FavouriteCurrenciesEffect
    data object UnableToDelete : FavouriteCurrenciesEffect
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
