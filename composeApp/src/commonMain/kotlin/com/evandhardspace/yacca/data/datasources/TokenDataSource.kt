package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.domain.repositories.Cleanable
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

internal interface TokenDataSource: Cleanable {
    suspend fun saveAccessToken(accessToken: String)
    suspend fun getAccessToken(): String?
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String?
}

internal class LocalEncryptedTokenDataSource(
    private val settings: Settings,
) : TokenDataSource {
    override suspend fun saveAccessToken(accessToken: String) {
        settings[ACCESS_TOKEN_KEY] = accessToken
    }

    override suspend fun getAccessToken(): String? =
        settings[ACCESS_TOKEN_KEY]

    override suspend fun saveRefreshToken(refreshToken: String) {
        settings[REFRESH_TOKEN_KEY] = refreshToken
    }

    override suspend fun getRefreshToken(): String? =
        settings[REFRESH_TOKEN_KEY]

    override suspend fun clear() {
        settings.clear()
    }
}

private const val ACCESS_TOKEN_KEY = "access_token"
private const val REFRESH_TOKEN_KEY = "refresh_token"