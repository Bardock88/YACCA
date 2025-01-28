package com.evandhardspace.yacca.utils

import kotlinx.coroutines.flow.Flow

internal interface NetworkMonitor {
    val isConnected: Flow<Boolean>
}
