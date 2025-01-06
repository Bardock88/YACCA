package com.evandhardspace.yacca

import com.evandhardspace.yacca.data.currency.CurrencyService
import com.evandhardspace.yacca.data.currency.DefaultCurrencyDataSource
import com.evandhardspace.yacca.data.user.DefaultUserDataSource
import com.evandhardspace.yacca.db.initDatabase
import com.evandhardspace.yacca.plugins.*
import com.evandhardspace.yacca.security.hashing.SHA256HashingService
import com.evandhardspace.yacca.security.token.JwtTokenService
import com.evandhardspace.yacca.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlin.time.Duration.Companion.days

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    initDatabase()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365.days.inWholeMilliseconds, // todo extract
        secret = environment.config.property("jwt.secret").getString(),
    )
    val client = buildHttpClient()

    val tokenService = JwtTokenService(config = tokenConfig)
    val hashingService = SHA256HashingService()
    val currencyService = CurrencyService(client = client)

    val userDataSource = DefaultUserDataSource()
    val currencyDataSource = DefaultCurrencyDataSource(
        currencyService = currencyService,
    )

    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting(
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        currencyDataSource = currencyDataSource,
    )
}
