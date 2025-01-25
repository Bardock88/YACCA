package com.evandhardspace.yacca.presentation.favourites

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
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
) : EffectViewModel<FavouriteCurrenciesEffect>() {

    private val _viewState = MutableStateFlow<CurrencyState>(CurrencyState.Loading)
    val viewState = _viewState.asStateFlow()

    init {
        currencyRepository.allCurrencies()
            .map { it.filter { currency -> currency.isFavourite } }
            .onEach { currencies ->
                _viewState.update {
                    CurrencyState.CurrencyLoaded(
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
                }
            }.launchIn(viewModelScope)
        viewModelScope.launch {
            currencyRepository.fetchCurrencies()
        }
    }

    fun logout() {
        viewModelScope.launch {
            cleanUpManager.clear()
            _viewState.update { CurrencyState.Loading }
            FavouriteCurrenciesEffect.LoggedOut.send()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _viewState.update { CurrencyState.Loading }
            currencyRepository.fetchCurrencies().fold(
                onSuccess = { /* todo */ },
                onFailure = { /* todo */ },
            )
        }
    }

    fun deleteFromFavourites(currencyId: String) {
        viewModelScope.launch {
            currencyRepository.deleteFromFavourites(currencyId)
                .fold(
                    onSuccess = { /* todo */ },
                    onFailure = { /* todo */ },
                )
        }
    }
}

internal sealed interface FavouriteCurrenciesEffect : Effect {
    data object LoggedOut : FavouriteCurrenciesEffect
}
