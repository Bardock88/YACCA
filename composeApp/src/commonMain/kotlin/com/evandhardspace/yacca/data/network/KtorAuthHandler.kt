package com.evandhardspace.yacca.data.network

import com.evandhardspace.yacca.data.datasources.TokenDataSource
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth

internal suspend fun HttpRequestBuilder.auth(
    tokenDataSource: TokenDataSource,
) {
    tokenDataSource.getAccessToken()?.let { token ->
        bearerAuth(token)
    }
}