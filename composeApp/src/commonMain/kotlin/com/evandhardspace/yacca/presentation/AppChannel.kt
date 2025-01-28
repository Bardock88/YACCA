package com.evandhardspace.yacca.presentation

import com.evandhardspace.yacca.utils.Effect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal class AppEffectChannel : SessionChannel, SnackbarChannel {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _effect = Channel<AppEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effect: Flow<AppEffect> = _effect
        .receiveAsFlow()
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
        )
    override val sessionEffect: Flow<AppEffect.SessionEffect>
         = effect.filterIsInstance()

    override val snackbarEffect: Flow<AppEffect.SnackbarEffect>
            = effect.filterIsInstance()

    override suspend fun send(effect: AppEffect.SessionEffect): Unit =
        _effect.send(effect)

    override suspend fun send(effect: AppEffect.SnackbarEffect): Unit =
        _effect.send(effect)
}

internal interface SessionReceiveChannel {
    val sessionEffect: Flow<AppEffect.SessionEffect>
}
internal fun interface SessionSendChannel {
    suspend fun send(effect: AppEffect.SessionEffect)
}
internal interface SessionChannel: SessionReceiveChannel, SessionSendChannel

internal interface SnackbarReceiveChannel {
    val snackbarEffect: Flow<AppEffect.SnackbarEffect>
}
internal fun interface SnackbarSendChannel {
    suspend fun send(effect: AppEffect.SnackbarEffect)
}
internal interface SnackbarChannel: SnackbarReceiveChannel, SnackbarSendChannel


internal sealed interface AppEffect : Effect {
    sealed interface SessionEffect : AppEffect {
        data object UserIsSignedOut : SessionEffect
    }

    sealed interface SnackbarEffect : AppEffect {
        val message: String

        data class Error(override val message: String) : SnackbarEffect
        data class Success(override val message: String) : SnackbarEffect
        data class General(override val message: String) : SnackbarEffect
    }
}

internal enum class SnackbarState {
    Error, General, Success,
}

internal fun generalSnackbar(message: String): AppEffect.SnackbarEffect.General =
    AppEffect.SnackbarEffect.General(message)

internal fun errorSnackbar(message: String): AppEffect.SnackbarEffect.Error =
    AppEffect.SnackbarEffect.Error(message)

internal fun successSnackbar(message: String): AppEffect.SnackbarEffect.Success =
    AppEffect.SnackbarEffect.Success(message)
