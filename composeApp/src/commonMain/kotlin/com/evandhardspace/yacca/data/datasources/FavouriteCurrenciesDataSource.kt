package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.response.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
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
//            tokenDataSource.getAccessToken()?.let { token ->
//                bearerAuth(token) // todo extract to client setup logic
//            }
        }.body<List<CurrencyResponse>>()
    }
}
