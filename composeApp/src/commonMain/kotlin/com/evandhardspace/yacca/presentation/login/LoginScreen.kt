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
            text = if (state.isSignUpFlow) "Sign Up" else "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = { Text("Email") },
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
            label = { Text("Password") },
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
                label = { Text("Confirm Password") },
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

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isLoading.not() && (state.errorMessage == null) && state.isDataFilled
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
            } else {
                Text(text = if (state.isSignUpFlow) "Create Account" else "Log In")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (state.isSignUpFlow) "Already have an account? Sign in" else "Don't have an account? Sign up",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onToggleLoginFlow)
        )
    }
}

