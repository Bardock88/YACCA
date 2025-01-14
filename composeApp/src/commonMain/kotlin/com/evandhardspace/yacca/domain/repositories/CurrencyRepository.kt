package com.evandhardspace.yacca.domain.repositories

import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.domain.models.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class CurrencyRepository(
    private val currencyDataSource: CurrencyDataSource,
) : Cleanable {

    suspend fun allCurrencies(): Result<List<Currency>> = withContext(Dispatchers.IO) {
        runCatching {
            currencyDataSource.allCurrencies().map {
                Currency(
                    id = it.id,
                    name = it.name,
                    symbol = it.symbol,
                    priceUsd = it.priceUsd,
                    isFavourite = false,
                )
            }
        }
    }

    suspend fun getFavouriteCurrencies(): Result<List<Currency>> = withContext(Dispatchers.IO) {
        runCatching {
            currencyDataSource.getFavouriteCurrencies().map {
                Currency(
                    id = it.id,
                    name = it.name,
                    symbol = it.symbol,
                    priceUsd = it.priceUsd,
                    isFavourite = true,
                )
            }
        }
    }

    suspend fun addToFavourites(currencyId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                currencyDataSource.addToFavourites(currencyId)
            }
        }

    override suspend fun clear() {
        /* no-op yet */
    }
}
