package com.evandhardspace.yacca.data.datasources

import com.evandhardspace.yacca.BASE_URL
import com.evandhardspace.yacca.Endpoints
import com.evandhardspace.yacca.data.database.CurrencyDao
import com.evandhardspace.yacca.data.database.CurrencyEntity
import com.evandhardspace.yacca.domain.models.Currency
import com.evandhardspace.yacca.domain.repositories.Cleanable
import com.evandhardspace.yacca.request.FavouriteCurrencyRequest
import com.evandhardspace.yacca.response.CurrencyResponse
import com.evandhardspace.yacca.response.UserCurrencyResponse
import com.evandhardspace.yacca.utils.client.NetworkClient
import com.evandhardspace.yacca.utils.client.NetworkResult
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal interface CurrencyDataSource {
    suspend fun allCurrencies(): List<CurrencyResponse>
    suspend fun allUserCurrencies(): List<UserCurrencyResponse>
    suspend fun getFavouriteCurrencies(): List<CurrencyResponse>
    suspend fun addToFavourites(currencyId: String)
    suspend fun deleteFromFavourites(currencyId: String)
}

internal class LocalCurrenciesDataSource(
    private val currencyDao: CurrencyDao,
) : Cleanable {

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

    suspend fun updateFavourite(id: String, isFavourite: Boolean): Unit =
        currencyDao.updateFavourite(id, isFavourite)

    override suspend fun clear() {
        currencyDao.clearAll()
    }

}

internal class NetworkCurrencyDataSource(
    private val networkClient: NetworkClient,
) : CurrencyDataSource {
    override suspend fun allCurrencies(): List<CurrencyResponse> = networkClient
        .get<List<CurrencyResponse>>("$BASE_URL/${Endpoints.CURRENCIES_PUBLIC_INFO}")
        .getOrThrow()
        .body

    override suspend fun allUserCurrencies(): List<UserCurrencyResponse> = networkClient
        .get<List<UserCurrencyResponse>>("$BASE_URL/${Endpoints.CURRENCIES_USER_INFO}", withAuth = true)
        .getOrThrow()
        .body

    override suspend fun getFavouriteCurrencies(): List<CurrencyResponse> = networkClient
        .get<List<CurrencyResponse>>("$BASE_URL/${Endpoints.FAVOURITES}")
        .getOrThrow()
        .body

    override suspend fun addToFavourites(currencyId: String) {
        val result: NetworkResult<Unit> = networkClient.post(
            "$BASE_URL/${Endpoints.FAVOURITES}",
            withAuth = true,
            body = FavouriteCurrencyRequest(currencyId),
        )
        if (result.getOrThrow().isResponseSuccessful.not()) error("currency was not added to favourites") // todo add domain backend exception
    }

    override suspend fun deleteFromFavourites(currencyId: String) {
        val result: NetworkResult<Unit> = networkClient.delete(
            "$BASE_URL/${Endpoints.FAVOURITES}/$currencyId",
            withAuth = true,
        )
        if (result.getOrThrow().isResponseSuccessful.not()) error("currency was not added to favourites") // todo add domain backend exception
    }
}
