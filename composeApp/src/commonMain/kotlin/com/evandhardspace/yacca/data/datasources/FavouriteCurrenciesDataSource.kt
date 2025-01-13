package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.data.network.auth
import com.evandhardspace.yacca.response.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal interface FavouriteCurrenciesDataSource {

    suspend fun getFavouriteCurrencies(): List<CurrencyResponse>
}

internal class NetworkFavouriteCurrenciesDataSource(
    private val client: HttpClient,
    private val tokenDataSource: TokenDataSource,
) : FavouriteCurrenciesDataSource {
    override suspend fun getFavouriteCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/favourites") {
            auth(tokenDataSource)
        }.body<List<CurrencyResponse>>()
    }
}
