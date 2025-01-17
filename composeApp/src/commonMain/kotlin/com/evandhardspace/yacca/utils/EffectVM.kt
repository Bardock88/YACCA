package com.evandhardspace.yacca.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn

interface Effect

abstract class EffectViewModel<E : Effect> : ViewModel() {
    private val _effect = Channel<E>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effect: Flow<E> = _effect
        .receiveAsFlow()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        )

    protected suspend fun E.send() = _effect.send(this)
}

@Composable
fun <E : Effect> OnEffect(effect: Flow<E>, onEffect: suspend (E) -> Unit): Unit =
    LaunchedEffect(Unit) { effect.collect(onEffect) }