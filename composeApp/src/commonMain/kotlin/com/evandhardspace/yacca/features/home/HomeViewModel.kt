package com.evandhardspace.yacca.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.repositories.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch { updateAllCurrencies() }
    }

    fun refresh() {
        viewModelScope.launch {
            _viewState.value = HomeScreenState.Loading
            updateAllCurrencies()
        }
    }

    private suspend fun updateAllCurrencies() {
        _viewState.update {
            currencyRepository.allCurrencies()
                .map { currencies ->
                    currencies.map {
                        CurrencyUi(
                            id = it.id,
                            name = it.name,
                            symbol = it.symbol,
                            priceUsd = it.priceUsd,
                            isFavourite = it.isFavourite,
                        )
                    }
                }
                .fold(
                    onSuccess = { currencies ->
                        HomeScreenState.CurrencyLoaded(
                            currencies = currencies,
                            isUserLogged = false,
                        )
                    },
                    onFailure = { HomeScreenState.Error }
                )
        }
    }
}