package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.data.database.CurrencyDao
import com.evandhardspace.yacca.data.database.CurrencyEntity
import com.evandhardspace.yacca.domain.models.Currency
import com.evandhardspace.yacca.request.FavouriteCurrencyRequest
import com.evandhardspace.yacca.response.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

internal interface CurrencyDataSource {
    suspend fun allCurrencies(): List<CurrencyResponse>
    suspend fun getFavouriteCurrencies(): List<CurrencyResponse>
    suspend fun addToFavourites(currencyId: String)
}

internal class LocalCurrenciesDataSource(
    private val currencyDao: CurrencyDao,
) {

    fun allCurrencies(): Flow<List<Currency>> = currencyDao.getAll().map {
        it.map { currency ->
            Currency(
                currency.id,
                currency.name,
                currency.symbol,
                currency.priceUsd,
                currency.isFavourite,
            )
        }
    }

    // todo transaction
    fun favouriteCurrencies(): Flow<List<Currency>> = currencyDao
        .getAll()
        .map {
            it.filter { currency -> currency.isFavourite }
                .map { currency ->
                    Currency(
                        currency.id,
                        currency.name,
                        currency.symbol,
                        currency.priceUsd,
                        currency.isFavourite,
                    )
                }
        }

    suspend fun clearAndSaveCurrencies(currencies: List<Currency>) {
        currencyDao.clearAll()
        currencyDao.insertAll(currencies.map {
            CurrencyEntity(
                it.id,
                it.name,
                it.symbol,
                it.priceUsd,
                it.isFavourite,
            )
        })
    }

    suspend fun deleteCurrency(id: String): Unit =
        currencyDao.deleteById(id)

    suspend fun clearAll(): Unit =
        currencyDao.clearAll()

    suspend fun updateFavourite(id: String, isFavourite: Boolean): Unit =
        currencyDao.updateFavourite(id, isFavourite)

}

internal class NetworkCurrencyDataSource(
    private val client: HttpClient,
) : CurrencyDataSource {

    override suspend fun allCurrencies(): List<CurrencyResponse> {
        return client.get("$BASE_URL/currencies/public-info")
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
