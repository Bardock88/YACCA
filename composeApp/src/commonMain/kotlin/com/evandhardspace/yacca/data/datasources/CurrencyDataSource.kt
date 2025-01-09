package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.response.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface CurrencyDataSource {
    suspend fun allCurrencies(): List<CurrencyResponse>
}

class NetworkCurrencyDataSource(private val client: HttpClient): CurrencyDataSource {
    override suspend fun allCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/currencies")
            .body<List<CurrencyResponse>>()
    }
}
