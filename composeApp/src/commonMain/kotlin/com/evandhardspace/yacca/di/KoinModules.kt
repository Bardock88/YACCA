package com.evandhardspace.yacca.di

import com.evandhardspace.yacca.features.home.HomeViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkModule = module {
    single { HttpClient() }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
}

val appModule
    get() = listOf(
        networkModule,
        viewModelModule,
    )
