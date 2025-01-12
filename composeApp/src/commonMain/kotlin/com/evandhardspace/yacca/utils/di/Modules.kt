package com.evandhardspace.yacca.utils.di

import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.data.datasources.LocalUserDataSource
import com.evandhardspace.yacca.data.datasources.NetworkCurrencyDataSource
import com.evandhardspace.yacca.data.datasources.UserDataSource
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository
import com.evandhardspace.yacca.presentation.home.HomeViewModel
import com.evandhardspace.yacca.presentation.navigation.NavigationViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
        }
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::NavigationViewModel)
}

val dataSourceModule = module {
    factoryOf(::NetworkCurrencyDataSource) bind CurrencyDataSource::class
    factoryOf(::LocalUserDataSource) bind UserDataSource::class
}

val repositoryModule = module {
    factoryOf(::CurrencyRepository)
    factoryOf(::UserRepository)
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
