package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.data.network.auth
import com.evandhardspace.yacca.response.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal interface CurrencyDataSource {
    suspend fun allCurrencies(): List<CurrencyResponse>
    suspend fun getFavouriteCurrencies(): List<CurrencyResponse>
}

internal class NetworkCurrencyDataSource(
    private val client: HttpClient,
    private val tokenDataSource: TokenDataSource,
) : CurrencyDataSource {

    override suspend fun allCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/currencies")
            .body<List<CurrencyResponse>>()
    }

    override suspend fun getFavouriteCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/favourites") {
            auth(tokenDataSource)
        }.body<List<CurrencyResponse>>()
    }
}
