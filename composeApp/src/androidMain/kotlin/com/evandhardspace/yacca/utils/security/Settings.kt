package com.evandhardspace.yacca.utils.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

fun provideSettings(context: Context?): Settings {
    requireNotNull(context)

    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedPreferences = EncryptedSharedPreferences.create(
        context,
        context.packageName + "_encrypted_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    return SharedPreferencesSettings(encryptedPreferences)
}