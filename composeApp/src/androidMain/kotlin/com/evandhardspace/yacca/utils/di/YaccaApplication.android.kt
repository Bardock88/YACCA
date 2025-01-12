package com.evandhardspace.yacca.utils.di

import androidx.compose.runtime.Composable

@Composable
actual fun YaccaApplication(content: @Composable () -> Unit): Unit = content()