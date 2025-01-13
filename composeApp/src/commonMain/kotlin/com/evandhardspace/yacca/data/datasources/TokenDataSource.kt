package com.evandhardspace.yacca.data.datasources

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

internal interface TokenDataSource {
    suspend fun saveAccessToken(accessToken: String)
    suspend fun getAccessToken(): String?
    suspend fun clearTokens()
}

internal class LocalEncryptedTokenDataSource(
    private val settings: Settings,
) : TokenDataSource {
    override suspend fun saveAccessToken(accessToken: String) {
        settings[ACCESS_TOKEN_KEY] = accessToken
    }

    override suspend fun getAccessToken(): String? =
        settings[ACCESS_TOKEN_KEY]

    override suspend fun clearTokens() {
        settings.clear()
    }
}

private const val ACCESS_TOKEN_KEY = "access_token"