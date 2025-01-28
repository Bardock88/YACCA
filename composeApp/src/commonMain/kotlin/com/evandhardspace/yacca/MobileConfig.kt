package com.evandhardspace.yacca

object MobileConfig {
    val BASE_URL: String = if(IS_EMULATOR_LOCAL_HOST_CONFIG) {
        "$localHost:$SERVER_LOCAL_PORT_CONFIG"
    } else BASE_URL_CONFIG.takeUnless { it.isEmpty() }
        ?: error("emulator.localhost is false but local property server.host is not set")
}

// workaround, since BuildKonfig has issues in android implementations
internal expect val BASE_URL_CONFIG: String
internal expect val SERVER_LOCAL_PORT_CONFIG: Int
internal expect val IS_EMULATOR_LOCAL_HOST_CONFIG: Boolean