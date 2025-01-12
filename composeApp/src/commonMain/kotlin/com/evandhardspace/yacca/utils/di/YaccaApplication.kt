package com.evandhardspace.yacca.utils.di

import androidx.compose.runtime.Composable

@Composable
expect fun YaccaApplication(
    content: @Composable () -> Unit
)