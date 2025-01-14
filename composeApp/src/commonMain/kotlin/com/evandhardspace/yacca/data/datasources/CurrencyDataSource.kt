package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.request.FavouriteCurrencyRequest
import com.evandhardspace.yacca.response.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import org.lighthousegames.logging.logging

internal interface CurrencyDataSource {
    suspend fun allCurrencies(): List<CurrencyResponse>
    suspend fun getFavouriteCurrencies(): List<CurrencyResponse>
    suspend fun addToFavourites(currencyId: String)
}

internal class NetworkCurrencyDataSource(
    private val client: HttpClient,
) : CurrencyDataSource {

    override suspend fun allCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/currencies")
            .body<List<CurrencyResponse>>()
    }

    override suspend fun getFavouriteCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/favourites") {
        }.body<List<CurrencyResponse>>()
    }

    override suspend fun addToFavourites(currencyId: String) {
        val result = client.post("$BASE_URL/favourites") {
                setBody(
                    FavouriteCurrencyRequest(currencyId)
                )
            }
        if (result.status.isSuccess().not()) error("currency was not added to favourites") // todo add domain backend exception
    }
}
