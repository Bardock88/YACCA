package com.evandhardspace.yacca.presentation.favourites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.data.datasources.FavouriteCurrenciesDataSource
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import kotlinx.coroutines.launch

// todo remove test
internal class FavouriteCurrenciesViewModel(
    private val cleanUpManager: CleanUpManager,
    private val ds: FavouriteCurrenciesDataSource
) : EffectViewModel<FavouriteCurrenciesEffect>() {

    var currenciesState by mutableStateOf("_")

    fun logout() {
        viewModelScope.launch {
            cleanUpManager.clear()
            FavouriteCurrenciesEffect.LoggedOut.send()
        }
    }

    fun test() {
        viewModelScope.launch {
            currenciesState = ds.getFavouriteCurrencies().toString()
        }
    }
}

internal sealed interface FavouriteCurrenciesEffect : Effect {
    data object LoggedOut : FavouriteCurrenciesEffect
}
