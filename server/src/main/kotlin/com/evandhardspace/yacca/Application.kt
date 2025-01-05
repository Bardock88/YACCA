package com.evandhardspace.yacca

import com.evandhardspace.yacca.data.user.InMemoryUserDataSource
import com.evandhardspace.yacca.plugins.configureMonitoring
import com.evandhardspace.yacca.plugins.configureRouting
import com.evandhardspace.yacca.plugins.configureSecurity
import com.evandhardspace.yacca.plugins.configureSerialization
import com.evandhardspace.yacca.security.hashing.SHA256HashingService
import com.evandhardspace.yacca.security.token.JwtTokenService
import com.evandhardspace.yacca.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlin.time.Duration.Companion.days

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val userDataSource = InMemoryUserDataSource()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365.days.inWholeMilliseconds,
        secret = environment.config.property("jwt.secret").getString(),
    )
    val tokenService = JwtTokenService(tokenConfig)
    val hashingService = SHA256HashingService()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting(
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
    )
}