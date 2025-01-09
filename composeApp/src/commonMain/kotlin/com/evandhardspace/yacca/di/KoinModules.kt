package com.evandhardspace.yacca.di

import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.data.datasources.NetworkCurrencyDataSource
import com.evandhardspace.yacca.features.home.HomeViewModel
import com.evandhardspace.yacca.repositories.CurrencyRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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
}

val dataSourceModule = module {
    factoryOf(::NetworkCurrencyDataSource) bind CurrencyDataSource::class
}

val repositoryModule = module {
    factoryOf(::CurrencyRepository)
}

val appModule
    get() = listOf(
        networkModule,
        viewModelModule,
        dataSourceModule,
        repositoryModule,
    )
