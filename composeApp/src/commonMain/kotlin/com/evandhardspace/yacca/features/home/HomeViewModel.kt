package com.evandhardspace.yacca.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.Currency
import com.evandhardspace.yacca.repositories.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(listOf<Currency>())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            _viewState.update { currencyRepository.allCurrencies() }
        }
    }
}