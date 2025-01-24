package com.evandhardspace.yacca

object MobileConfig {
    val BASE_URL: String = if(BuildKonfig.isEmulatorLocalhost) {
        "$localHost:${BuildKonfig.serverLocalPort}"
    } else BuildKonfig.baseUrl.takeUnless { it.isEmpty() }
        ?: error("emulator.localhost is false but local property server.host is not set")
}