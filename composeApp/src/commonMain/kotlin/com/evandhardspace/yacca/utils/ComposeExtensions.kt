package com.evandhardspace.yacca.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity

internal val Int.pxAsDp
    @ReadOnlyComposable
    @Composable
    get() = with(LocalDensity.current) { toFloat().toDp() }