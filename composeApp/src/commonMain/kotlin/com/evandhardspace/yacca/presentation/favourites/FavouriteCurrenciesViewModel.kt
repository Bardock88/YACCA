package com.evandhardspace.yacca.presentation.favourites

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import kotlinx.coroutines.launch

internal class FavouriteCurrenciesViewModel(
    private val cleanUpManager: CleanUpManager,
) : EffectViewModel<FavouriteCurrenciesEffect>() {

    fun logout() {
        viewModelScope.launch {
            cleanUpManager.clear()
            FavouriteCurrenciesEffect.LoggedOut.send()
        }
    }
}

internal sealed interface FavouriteCurrenciesEffect : Effect {
    data object LoggedOut : FavouriteCurrenciesEffect
}
