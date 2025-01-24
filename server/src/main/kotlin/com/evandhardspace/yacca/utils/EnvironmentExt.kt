package com.evandhardspace.yacca.utils

import com.evandhardspace.yacca.Config

internal fun safeSystemGetEnv(name: String, default: String): String {
    val prop = System.getenv(name)

    return if (Config.isLocalImplementation) {
        prop ?: default
    } else requireNotNull(prop) { "property $name is required" }
}