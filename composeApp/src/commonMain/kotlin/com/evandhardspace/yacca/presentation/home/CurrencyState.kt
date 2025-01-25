package com.evandhardspace.yacca.presentation.home

import com.evandhardspace.yacca.utils.Effect

internal data class HomeScreenState(
    val currencyState: CurrencyState,
    val isUserLoggedIn: Boolean,
)

internal sealed interface CurrencyState {

    data object Loading : CurrencyState

    data class CurrencyLoaded(
        val currencies: List<CurrencyUi>,
    ) : CurrencyState

    data object Error : CurrencyState
}

internal sealed interface HomeScreenEffect : Effect {
    data object UnableToUpdate : HomeScreenEffect
    data object UnableToAdd : HomeScreenEffect
    data object UnableToDelete : HomeScreenEffect
}