package com.evandhardspace.yacca.repositories

import com.evandhardspace.yacca.data.datasources.CurrencyDataSource
import com.evandhardspace.yacca.domain.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CurrencyRepository(
    private val dataSource: CurrencyDataSource,
) {

    suspend fun allCurrencies(): Result<List<Currency>> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.allCurrencies().map {
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
}
