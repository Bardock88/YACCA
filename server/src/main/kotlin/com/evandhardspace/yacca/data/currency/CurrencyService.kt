package com.evandhardspace.yacca.data.currency

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class CurrencyService(
    private val client: HttpClient,
) {
    suspend fun allCurrencies(): CurrenciesResource = runCatching {
        client.get("https://api.coincap.io/v2/assets") {
            contentType(ContentType.Application.Json)
        }.body<CurrenciesResource>()
    }.getOrElse { currenciesResourceDefault }

    suspend fun filteredCurrencies(currencyIds: Set<String>): CurrenciesResource =
        runCatching {
            client.get("https://api.coincap.io/v2/assets") {
                parameter("ids", currencyIds.joinToString(","))
            }.body<CurrenciesResource>()
        }.getOrElse {
            currenciesResourceDefault.copy(
                data = currenciesResourceDefault
                    .data
                    .filter { it.id in currencyIds }
            )
        }
}
