package com.evandhardspace.yacca.utils.di

import com.evandhardspace.yacca.data.datasources.AuthDataSource
import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.data.datasources.FavouriteCurrenciesDataSource
import com.evandhardspace.yacca.data.datasources.LocalEncryptedTokenDataSource
import com.evandhardspace.yacca.data.datasources.LocalUserDataSource
import com.evandhardspace.yacca.data.datasources.NetworkAuthDataSource
import com.evandhardspace.yacca.data.datasources.NetworkCurrencyDataSource
import com.evandhardspace.yacca.data.datasources.NetworkFavouriteCurrenciesDataSource
import com.evandhardspace.yacca.data.datasources.TokenDataSource
import com.evandhardspace.yacca.data.datasources.UserDataSource
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.domain.repositories.AuthRepository
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.presentation.favourites.FavouriteCurrenciesViewModel
import com.evandhardspace.yacca.presentation.home.HomeViewModel
import com.evandhardspace.yacca.presentation.login.LoginViewModel
import com.evandhardspace.yacca.presentation.navigation.NavigationViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::NavigationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::FavouriteCurrenciesViewModel)
}

val dataSourceModule = module {
    factoryOf(::NetworkCurrencyDataSource) bind CurrencyDataSource::class
    factoryOf(::NetworkAuthDataSource) bind AuthDataSource::class
    factoryOf(::NetworkFavouriteCurrenciesDataSource) bind FavouriteCurrenciesDataSource::class
    factoryOf(::LocalUserDataSource) bind UserDataSource::class
    factoryOf(::LocalEncryptedTokenDataSource) bind TokenDataSource::class
}

val repositoryModule = module {
    factoryOf(::CurrencyRepository)
    factoryOf(::UserRepository)
    factoryOf(::AuthRepository)
    factoryOf(::CleanUpManager)
}

expect val platformModule: Module

val appModule
    get() = listOf(
        networkModule,
        viewModelModule,
        dataSourceModule,
        repositoryModule,
        platformModule,
    )
