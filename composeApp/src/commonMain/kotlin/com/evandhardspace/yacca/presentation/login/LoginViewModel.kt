package com.evandhardspace.yacca.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import kotlinx.coroutines.launch


internal class LoginViewModel(
    private val userRepository: UserRepository,
) : EffectViewModel<LoginEffect>() {


    fun login() {
        viewModelScope.launch {
            userRepository.setUserLogged(true)
            LoginEffect.LoggedIn.send()
        }
    }
}

internal sealed interface LoginEffect: Effect {
    data object LoggedIn: LoginEffect
}