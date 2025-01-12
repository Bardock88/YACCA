package com.evandhardspace.yacca.presentation.login

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.evandhardspace.yacca.utils.OnEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
    onDismiss: () -> Unit,
) {
    OnEffect(viewModel.effect) { effect ->
        when(effect) {
            LoginEffect.LoggedIn -> onDismiss()
        }
    }

    Box(modifier = modifier) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { viewModel.login() },
        ) { Text("Login") }
    }
}
