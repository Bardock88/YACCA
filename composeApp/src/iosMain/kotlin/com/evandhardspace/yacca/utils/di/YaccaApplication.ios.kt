package com.evandhardspace.yacca.utils.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication

@Composable
actual fun YaccaApplication(content: @Composable () -> Unit): Unit =
    KoinApplication(
        application = { modules(appModule) },
        content = content,
    )
