package com.evandhardspace.yacca.domain.repositories

import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.data.datasources.LocalCurrenciesDataSource
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
) : Cleanable {

    fun allCurrencies(): Flow<List<Currency>> =
        localCurrenciesDataSource.allCurrencies()

    // todo
    suspend fun fetchCurrencies(): Result<Unit> = runCatching {
        val remoteCurrencies = currencyDataSource.allCurrencies()
        val localFavorites = localCurrenciesDataSource.allCurrencies()
            .first()
            .filter { it.isFavourite }
            .associateBy { it.id }

        // Map remote data into local entities, preserving favorite status
        val updatedCurrencies = remoteCurrencies.map { remote ->
            Currency(
                id = remote.id,
                name = remote.name,
                symbol = remote.symbol,
                priceUsd = remote.priceUsd,
                isFavourite = localFavorites[remote.id]?.isFavourite ?: false
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
                }
                Unit
            }
        }

    override suspend fun clear() {
        /* no-op yet */
    }
}
