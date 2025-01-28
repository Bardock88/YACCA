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
        validateInputs()
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _viewState.update { it.copy(confirmPassword = newConfirmPassword) }
        validateInputs()
    }

    fun toggleLoginFlow() {
        _viewState.update {
            it.copy(
                isSignUpFlow = !it.isSignUpFlow,
                confirmPassword = "",
                error = null,
            )
        }
        validateInputs()
    }

    fun onSubmit() {
        val currentState = _viewState.value
        if (validateInputs().not()) return

        _viewState.update { it.copy(isLoading = true, error = null) }

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
                            error = LoginError.Unknown
                        )
                    }
                }
            )
        }
    }

    private fun validateInputs(): Boolean {
        val currentState = _viewState.value

        val emailError = if (currentState.email.isBlank()) {
            LoginError.BlankEmail
        } else null

        val passwordError = if (currentState.password.length < 8) {
            LoginError.PasswordShort
        } else null

        val confirmPasswordError =
            if (currentState.isSignUpFlow && currentState.password != currentState.confirmPassword) {
                LoginError.PasswordDontMatch
            } else null

        val error = emailError ?: passwordError ?: confirmPasswordError

        _viewState.update {
            it.copy(error = error)
        }

        return error == null
    }

    private suspend fun login() {
        userRepository.setUserLogged(true)
        LoginEffect.LoggedIn.send()
        clear()
    }

    private fun clear() {
        _viewState.update { LoginState() }
    }
}

internal data class LoginState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUpFlow: Boolean = false,
    val isLoading: Boolean = false,
    val error: LoginError? = null,
    val success: Boolean = false
)

internal enum class LoginError {
    BlankEmail,
    PasswordShort,
    PasswordDontMatch,
    Unknown,
}

internal val LoginState.isDataFilled: Boolean
    get() = if (isSignUpFlow) {
        email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
    } else email.isNotBlank() && password.isNotBlank()

internal sealed interface LoginEffect : Effect {
    data object LoggedIn : LoginEffect
}