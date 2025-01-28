package com.evandhardspace.yacca.effecthandler

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.evandhardspace.yacca.presentation.AppEffect.*
import com.evandhardspace.yacca.presentation.SnackbarReceiveChannel
import com.evandhardspace.yacca.presentation.SnackbarState
import com.evandhardspace.yacca.utils.OnEffect
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun SnackbarHandler(
    snackbarHostState: SnackbarHostState,
    onSnackbarStateChange: (SnackbarState) -> Unit,
) {
    val snackbarChannel = koinInject<SnackbarReceiveChannel>()
    val coroutineScope = rememberCoroutineScope()
    var snackbarJob: Job? = remember { null }

    OnEffect(snackbarChannel.snackbarEffect) { effect ->
        when (effect) {
            is SnackbarEffect.Error -> {
                onSnackbarStateChange(SnackbarState.Error)
                snackbarJob?.cancel()
                snackbarJob = coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            is SnackbarEffect.General -> {
                onSnackbarStateChange(SnackbarState.General)
                snackbarJob?.cancel()
                snackbarJob = coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            is SnackbarEffect.Success -> {
                onSnackbarStateChange(SnackbarState.Success)
                snackbarJob?.cancel()
                snackbarJob = coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}
