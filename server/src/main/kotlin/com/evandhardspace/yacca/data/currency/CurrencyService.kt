package com.evandhardspace.yacca.data.currency

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class CurrencyService(
    private val client: HttpClient,
) {
    suspend fun allCurrencies(): CurrenciesResource =
        client.get("https://api.coincap.io/v2/assets").body()

    suspend fun filteredCurrencies(currencyIds: Set<String>): CurrenciesResource =
        client.get("https://api.coincap.io/v2/assets") {
            parameter("ids", currencyIds.joinToString(","))
        }.body()
}
