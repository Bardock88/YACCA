package com.evandhardspace.yacca.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evandhardspace.yacca.utils.OnEffect
import com.evandharpace.yacca.Res
import com.evandharpace.yacca.already_have_account
import com.evandharpace.yacca.confirm_password
import com.evandharpace.yacca.create_account
import com.evandharpace.yacca.dont_have_account
import com.evandharpace.yacca.email
import com.evandharpace.yacca.email_cannot_be_blank
import com.evandharpace.yacca.password
import com.evandharpace.yacca.password_dont_match
import com.evandharpace.yacca.short_password
import com.evandharpace.yacca.sign_in
import com.evandharpace.yacca.sign_up
import com.evandharpace.yacca.unknown_error
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
    onLoggedIn: () -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    OnEffect(viewModel.effect) { effect ->
        when (effect) {
            LoginEffect.LoggedIn -> onLoggedIn()
        }
    }

    Box(modifier = modifier) {
        SignInSignUpContent(
            state = state,
            onEmailChanged = viewModel::onEmailChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
            onSubmit = viewModel::onSubmit,
            onToggleLoginFlow = viewModel::toggleLoginFlow,
        )
    }
}

@Composable
private fun SignInSignUpContent(
    state: LoginState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleLoginFlow: () -> Unit, // todo rename
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(if (state.isSignUpFlow) Res.string.sign_up else Res.string.sign_in),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = { Text(stringResource(Res.string.email)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(Res.string.password)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = if (state.isSignUpFlow) ImeAction.Next else ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = state.isSignUpFlow) {
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                label = { Text(stringResource(Res.string.confirm_password)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.error != null) {
            Text(
                text = state.error.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isLoading.not() && (state.error == null) && state.isDataFilled
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
            } else {
                Text(text = stringResource(if (state.isSignUpFlow) Res.string.create_account else Res.string.sign_in))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(if (state.isSignUpFlow) Res.string.already_have_account else Res.string.dont_have_account),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onToggleLoginFlow)
        )
    }
}

@Composable
private fun LoginError.asString(): String = when(this) {
    LoginError.BlankEmail -> Res.string.email_cannot_be_blank
    LoginError.PasswordShort -> Res.string.short_password
    LoginError.PasswordDontMatch -> Res.string.password_dont_match
    LoginError.Unknown -> Res.string.unknown_error
}.let { stringResource(it) }
