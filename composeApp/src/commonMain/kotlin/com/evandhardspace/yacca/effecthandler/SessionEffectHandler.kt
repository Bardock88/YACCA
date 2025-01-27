package com.evandhardspace.yacca.effecthandler

import androidx.compose.runtime.Composable
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.presentation.AppEffect
import com.evandhardspace.yacca.presentation.SessionReceiveChannel
import com.evandhardspace.yacca.presentation.SnackbarSendChannel
import com.evandhardspace.yacca.utils.OnEffect
import org.koin.compose.koinInject

@Composable
fun SessionEffectHandler() {
    val cleanUpManager = koinInject<CleanUpManager>()
    val sessionChannel = koinInject<SessionReceiveChannel>()
    val snackbarChannel = koinInject<SnackbarSendChannel>()

    OnEffect(sessionChannel.sessionEffect) { effect ->
        when (effect) {
            AppEffect.SessionEffect.UserIsSignedOut -> {
                cleanUpManager.clear()
                snackbarChannel.send(AppEffect.SnackbarEffect.General("Your credential expired, please sign in again."))
            }
        }
    }
}
