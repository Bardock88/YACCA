package com.evandhardspace.yacca.presentation.favourites

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.presentation.home.CurrencyState
import com.evandhardspace.yacca.presentation.home.CurrencyUi
import kotlinx.coroutines.flow.update
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import com.evandhardspace.yacca.utils.formatToNDecimalPlaces
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class FavouriteCurrenciesViewModel(
    private val cleanUpManager: CleanUpManager,
    private val currencyRepository: CurrencyRepository,
) : EffectViewModel<FavouriteCurrenciesEffect>() {

    private val _viewState = MutableStateFlow<CurrencyState>(CurrencyState.Loading)
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch { updateFavouriteCurrencies() }
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
            updateFavouriteCurrencies()
        }
    }

    private suspend fun updateFavouriteCurrencies() {
        _viewState.update {
            currencyRepository.getFavouriteCurrencies().map { currencies ->
                currencies.map {
                    CurrencyUi(
                        id = it.id,
                        name = it.name,
                        symbol = it.symbol,
                        price = "$${it.priceUsd.formatToNDecimalPlaces(3)}",
                        isFavourite = it.isFavourite,
                    )
                }
            }
                .fold(
                    onSuccess = { currencies ->
                        CurrencyState.CurrencyLoaded(currencies)
                    },
                    onFailure = { CurrencyState.Error }
                )
        }
    }
}

internal sealed interface FavouriteCurrenciesEffect : Effect {
    data object LoggedOut : FavouriteCurrenciesEffect
}
