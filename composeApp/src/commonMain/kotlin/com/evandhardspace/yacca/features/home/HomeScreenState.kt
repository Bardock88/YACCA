package com.evandhardspace.yacca.features.home

internal sealed interface HomeScreenState {

    data object Loading: HomeScreenState

    data class CurrencyLoaded(
        val currencies: List<CurrencyUi>,
        val isUserLogged: Boolean,
    ): HomeScreenState

    data object Error: HomeScreenState
}