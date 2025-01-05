package com.evandhardspace.yacca.data.currency

import com.evandhardspace.yacca.data.response.CurrencyResponse
import com.evandhardspace.yacca.data.user.UserDataSource
import java.util.*

interface CurrencyDataSource {

    suspend fun allCurrencies(): List<CurrencyResponse>

    suspend fun addFavouriteCurrency(userId: UUID, currencyId: String): Boolean

    suspend fun deleteFavouriteCurrency(userId: UUID, currencyId: String): Boolean

    suspend fun getFavouriteCurrencies(userId: UUID): List<CurrencyResponse>?
}

//todo migrate to db
class InMemoryCurrencyDataSource(
    private val currencyService: CurrencyService,
    private val userDataSource: UserDataSource,
) : CurrencyDataSource {
    override suspend fun allCurrencies(): List<CurrencyResponse> = currencyService
        .allCurrencies()
        .data.map(CurrencyResource::asResponse)

    private val favouriteCurrencies: MutableMap<UUID, MutableSet<String>> = mutableMapOf()

    override suspend fun addFavouriteCurrency(userId: UUID, currencyId: String): Boolean {
        val user = userDataSource.getUser(userId) ?: return false

        favouriteCurrencies[user.id]?.add(currencyId) ?: run {
            favouriteCurrencies[user.id] = mutableSetOf(currencyId)
        }
        return true
    }

    override suspend fun deleteFavouriteCurrency(userId: UUID, currencyId: String): Boolean {
        val user = userDataSource.getUser(userId) ?: return false
        return favouriteCurrencies[user.id]?.remove(currencyId) ?: false
    }

    override suspend fun getFavouriteCurrencies(userId: UUID): List<CurrencyResponse>? {
        val user = userDataSource.getUser(userId) ?: return null
        val favouriteCurrenciesIds = favouriteCurrencies[user.id] ?: return emptyList()
        if (favouriteCurrenciesIds.isEmpty()) return emptyList()
        return currencyService.filteredCurrencies(favouriteCurrenciesIds)
            .data.map(CurrencyResource::asResponse)
    }
}

private fun CurrencyResource.asResponse(): CurrencyResponse =
    CurrencyResponse(
        id = id,
        name = name,
        symbol = symbol,
        priceUsd = priceUsd,
    )
