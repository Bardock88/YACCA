package com.evandhardspace.yacca.presentation.home

internal data class HomeScreenState(
    val currencyState: CurrencyState,
    val isUserLogged: Boolean,
)

internal sealed interface CurrencyState {

    data object Loading : CurrencyState

    data class CurrencyLoaded(
        val currencies: List<CurrencyUi>,
    ) : CurrencyState

    data object Error : CurrencyState
}