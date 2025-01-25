package com.evandhardspace.yacca.presentation

import com.evandhardspace.yacca.utils.Effect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class SessionRepository {

    private val scope = CoroutineScope(Dispatchers.Default) // todo check dispatcher

    private val _effect = Channel<SessionEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effect: Flow<SessionEffect> = _effect
        .receiveAsFlow()
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
        ) // todo check dispatcher

    suspend fun send(event: SessionEffect) = _effect.send(event)
}

sealed interface SessionEffect: Effect {
    data object UserIsSignedOut: SessionEffect
}