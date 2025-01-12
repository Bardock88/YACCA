package com.evandhardspace.yacca.presentation.login

import androidx.lifecycle.viewModelScope
import com.evandhardspace.yacca.domain.repositories.AuthRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.utils.Effect
import com.evandhardspace.yacca.utils.EffectViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


internal class LoginViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : EffectViewModel<LoginEffect>() {

    private val _viewState = MutableStateFlow(LoginState())
    val viewState = _viewState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _viewState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChanged(newPassword: String) {
        _viewState.update { it.copy(password = newPassword) }
        validatePasswords()
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _viewState.update { it.copy(confirmPassword = newConfirmPassword) }
        validatePasswords()
    }

    fun toggleLoginFlow() {
        _viewState.update {
            it.copy(
                isSignUpFlow = !it.isSignUpFlow,
                confirmPassword = "",
                errorMessage = null,
            )
        }
        validatePasswords()
    }

    fun onSubmit() {
        val currentState = _viewState.value
        if (validatePasswords().not()) return

        _viewState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = if (currentState.isSignUpFlow) authRepository.signUp(
                currentState.email,
                currentState.password,
            ) else authRepository.signIn(
                currentState.email,
                currentState.password,
            )

            result.fold(
                onSuccess = {
                    _viewState.update { it.copy(isLoading = false, success = true) }
                    login()
                },
                onFailure = { error ->
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "An unknown error occurred"
                        )
                    }
                }
            )
        }
    }

    // todo check
    private fun validatePasswords(): Boolean {
        var isValid = false
        _viewState.update { currentState ->
            val isPasswordValid = if (currentState.isSignUpFlow) {
                currentState.password.length >= 8 && currentState.password == currentState.confirmPassword
            } else {
                currentState.password.length >= 8
            }
            if (isPasswordValid.not()) {
                currentState.copy(errorMessage = "Passwords are invalid")
            } else {
                isValid = true
                currentState.copy(errorMessage = null)
            }
        }
        return isValid
    }

    private suspend fun login() {
        userRepository.setUserLogged(true)
        LoginEffect.LoggedIn.send()
    }
}

internal data class LoginState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUpFlow: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

internal val LoginState.isDataFilled: Boolean
    get() = if (isSignUpFlow) {
        email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
    } else email.isNotBlank() && password.isNotBlank()

internal sealed interface LoginEffect : Effect {
    data object LoggedIn : LoginEffect
}