package com.evandhardspace.yacca.presentation.favourites

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import kotlinx.coroutines.launch

internal class FavouriteCurrenciesViewModel(
    private val userRepository: UserRepository,
) : EffectViewModel<FavouriteCurrenciesEffect>() {

    fun logout() {
        viewModelScope.launch {
            userRepository.setUserLogged(false)
            FavouriteCurrenciesEffect.LoggedOut.send()
        }
    }
}

internal sealed interface FavouriteCurrenciesEffect: Effect {
    data object LoggedOut: FavouriteCurrenciesEffect
}