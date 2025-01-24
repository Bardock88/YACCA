package com.evandhardspace.yacca

import com.evandhardspace.yacca.data.currency.CurrencyService
import com.evandhardspace.yacca.data.currency.DatabaseCurrencyDataSource
import com.evandhardspace.yacca.data.currency.InMemoryCurrencyDataSource
import com.evandhardspace.yacca.data.token.DatabaseJwtTokenDataSource
import com.evandhardspace.yacca.data.token.InMemoryJwtTokenDataSource
import com.evandhardspace.yacca.data.token.JwtTokenDataSource
import com.evandhardspace.yacca.data.user.DatabaseUserDataSource
import com.evandhardspace.yacca.data.user.InMemoryUserDataSource
import com.evandhardspace.yacca.db.initDatabase
import com.evandhardspace.yacca.plugins.*
import com.evandhardspace.yacca.security.hashing.SHA256HashingService
import com.evandhardspace.yacca.security.token.JwtTokenService
import com.evandhardspace.yacca.security.token.TokenConfig
import com.evandhardspace.yacca.utils.safeSystemGetEnv
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    initDatabase()
    val accessTokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 15.minutes.inWholeMilliseconds,
        secret = safeSystemGetEnv("JWT_SECRET", "mock_jwt_secret_key"),
    )
    val refreshTokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 30.days.inWholeMilliseconds,
        secret = safeSystemGetEnv("JWT_REFRESH_SECRET", "mock_jwt_refresh_secret"),
    )
    val client = buildHttpClient()

    val jwtTokenDataSource: JwtTokenDataSource =
        if (Config.isLocalImplementation) InMemoryJwtTokenDataSource()
        else DatabaseJwtTokenDataSource()

    val tokenService = JwtTokenService(
        accessTokenConfig = accessTokenConfig,
        refreshTokenConfig = refreshTokenConfig,
        jwtTokenDataSource = jwtTokenDataSource,
    )
    val hashingService = SHA256HashingService()
    val currencyService = CurrencyService(client = client)

    val userDataSource =
        if (Config.isLocalImplementation) InMemoryUserDataSource()
        else DatabaseUserDataSource()
    val currencyDataSource =
        if (Config.isLocalImplementation) InMemoryCurrencyDataSource(currencyService)
        else DatabaseCurrencyDataSource(currencyService)

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
