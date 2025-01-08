package com.evandhardspace.yacca.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val client: HttpClient, // todo add repository
) : ViewModel() {

    private val _viewState = MutableStateFlow(listOf<String>())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)
            _viewState.update {
                listOf(
                    "First",
                    "Second",
                    client.get("https://yacca-9df0a5ceac73.herokuapp.com/").bodyAsText(),
                )
            }
        }
    }
}