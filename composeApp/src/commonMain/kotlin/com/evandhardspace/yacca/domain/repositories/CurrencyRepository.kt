package com.evandhardspace.yacca.domain.repositories

import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.data.datasources.LocalCurrenciesDataSource
import com.evandhardspace.yacca.data.datasources.UserDataSource
import com.evandhardspace.yacca.domain.models.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class CurrencyRepository(
    private val currencyDataSource: CurrencyDataSource,
    private val localCurrenciesDataSource: LocalCurrenciesDataSource,
    private val userDataSource: UserDataSource,
) {

    fun allCurrencies(): Flow<List<Currency>> =
        localCurrenciesDataSource.allCurrencies()

    fun favouriteCurrencies(): Flow<List<Currency>> =
        localCurrenciesDataSource
            .allCurrencies()
            .map { it.filter { currency -> currency.isFavourite } }

    // todo
    suspend fun fetchCurrencies(): Result<Unit> =
        if (userDataSource.isUserLoggedIn().first()) fetchLoggedInCurrencies()
        else fetchNotLoggedInCurrencies()


    private suspend fun fetchNotLoggedInCurrencies(): Result<Unit> = runCatching {
        val remoteCurrencies = currencyDataSource.allCurrencies()

        val updatedCurrencies = remoteCurrencies.map { remote ->
            Currency(
                id = remote.id,
                name = remote.name,
                symbol = remote.symbol,
                priceUsd = remote.priceUsd,
                isFavourite = false
            )
        }

        localCurrenciesDataSource.clearAndSaveCurrencies(updatedCurrencies)
    }

    private suspend fun fetchLoggedInCurrencies(): Result<Unit> = runCatching {
        val remoteCurrencies = currencyDataSource.allUserCurrencies()

        val updatedCurrencies = remoteCurrencies.map { remote ->
            Currency(
                id = remote.id,
                name = remote.name,
                symbol = remote.symbol,
                priceUsd = remote.priceUsd,
                isFavourite = remote.isFavourite,
            )
        }

        localCurrenciesDataSource.clearAndSaveCurrencies(updatedCurrencies)
    }

    // todo check
    suspend fun getFavouriteCurrencies(): Result<List<Currency>> = withContext(Dispatchers.IO) {
        runCatching {
            fetchCurrencies()
            localCurrenciesDataSource.favouriteCurrencies().first()
        }
    }

    suspend fun addToFavourites(currencyId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                localCurrenciesDataSource.updateFavourite(currencyId, true)
                val result = runCatching {
                    currencyDataSource.addToFavourites(currencyId)
                }
                result.onFailure {
                    localCurrenciesDataSource.updateFavourite(currencyId, false)
                    error("unable add to favourites")
                }
                Unit
            }
        }

    suspend fun deleteFromFavourites(currencyId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                localCurrenciesDataSource.updateFavourite(currencyId, false)
                val result = runCatching {
                    currencyDataSource.deleteFromFavourites(currencyId)
                }
                result.onFailure {
                    localCurrenciesDataSource.updateFavourite(currencyId, true)
                    error("unable delete from favourites")
                }
                Unit
            }
        }
}
