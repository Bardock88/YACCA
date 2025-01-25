package com.evandhardspace.yacca.utils.security

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSBundle

@OptIn(ExperimentalSettingsImplementation::class)
fun provideSettings(): Settings =
    KeychainSettings("${NSBundle.mainBundle.bundleIdentifier}.AUTH")
