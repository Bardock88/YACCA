package com.evandhardspace.yacca.plugins

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json

fun Application.buildHttpClient(): HttpClient = HttpClient(CIO) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(
            Json { ignoreUnknownKeys = true }
        )
    }
}.apply { this@buildHttpClient.monitor.subscribe(ApplicationStopping) { close() } }
