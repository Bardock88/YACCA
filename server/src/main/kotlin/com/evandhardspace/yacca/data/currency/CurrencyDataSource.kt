package com.evandhardspace.yacca.data.currency

import com.evandhardspace.yacca.data.response.CurrencyResponse

interface CurrencyDataSource {

    suspend fun allCurrencies(): List<CurrencyResponse>
}

class DefaultCurrencyDataSource(
    private val service: CurrencyService,
) : CurrencyDataSource {
    override suspend fun allCurrencies(): List<CurrencyResponse> = service
        .allCurrencies()
        .data.map {
            CurrencyResponse(
                id = it.id,
                name = it.name,
                symbol = it.symbol,
                priceUsd = it.priceUsd,
            )
        }
}
