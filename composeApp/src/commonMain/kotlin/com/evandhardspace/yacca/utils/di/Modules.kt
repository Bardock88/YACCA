package com.evandhardspace.yacca.utils.di

import com.evandhardspace.yacca.data.database.AppDatabase
import com.evandhardspace.yacca.data.database.CurrencyDao
import com.evandhardspace.yacca.data.database.provideDatabase
import com.evandhardspace.yacca.data.datasources.AuthDataSource
import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.data.datasources.LocalCurrenciesDataSource
import com.evandhardspace.yacca.data.datasources.LocalEncryptedTokenDataSource
import com.evandhardspace.yacca.data.datasources.LocalUserDataSource
import com.evandhardspace.yacca.data.datasources.NetworkAuthDataSource
import com.evandhardspace.yacca.data.datasources.NetworkCurrencyDataSource
import com.evandhardspace.yacca.data.datasources.TokenDataSource
import com.evandhardspace.yacca.data.datasources.UserDataSource
import com.evandhardspace.yacca.domain.CleanUpManager
import com.evandhardspace.yacca.domain.repositories.AuthRepository
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.presentation.SessionRepository
import com.evandhardspace.yacca.presentation.favourites.FavouriteCurrenciesViewModel
import com.evandhardspace.yacca.presentation.home.HomeViewModel
import com.evandhardspace.yacca.presentation.login.LoginViewModel
import com.evandhardspace.yacca.presentation.navigation.NavigationViewModel
import com.evandhardspace.yacca.utils.client.NetworkClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val networkModule = module {
    single {
        HttpClient {
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
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
    factoryOf(::NetworkClient)
}

private val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::NavigationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::FavouriteCurrenciesViewModel)
}

private val dataSourceModule = module {
    factoryOf(::NetworkCurrencyDataSource) bind CurrencyDataSource::class
    factoryOf(::NetworkAuthDataSource) bind AuthDataSource::class
    factoryOf(::LocalUserDataSource) bind UserDataSource::class
    factoryOf(::LocalEncryptedTokenDataSource) bind TokenDataSource::class
    factoryOf(::LocalCurrenciesDataSource)
}

private val repositoryModule = module {
    factoryOf(::CurrencyRepository)
    factoryOf(::UserRepository)
    factoryOf(::AuthRepository)
    factoryOf(::CleanUpManager)
    singleOf(::SessionRepository)
}

private val persistenceModule = module {
    single<AppDatabase> { provideDatabase(get()) }
    single<CurrencyDao> { get<AppDatabase>().currencyDao() }
}

expect val platformModule: Module

internal val appModule
    get() = listOf(
        networkModule,
        viewModelModule,
        dataSourceModule,
        repositoryModule,
        platformModule,
        persistenceModule,
    )
