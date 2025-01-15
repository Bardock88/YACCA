package com.evandhardspace.yacca.data.currency

import com.evandhardspace.yacca.response.CurrencyResponse
import com.evandhardspace.yacca.db.FavoriteCurrencies
import com.evandhardspace.yacca.response.UserCurrencyResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface CurrencyDataSource {

    suspend fun allCurrencies(): List<CurrencyResponse>

    suspend fun addFavouriteCurrency(userId: UUID, currencyId: String): Boolean

    suspend fun deleteFavouriteCurrency(userId: UUID, currencyId: String): Boolean

    suspend fun getFavouriteCurrencies(userId: UUID): List<CurrencyResponse>?

    suspend fun allUserCurrencies(userId: UUID): List<UserCurrencyResponse>?
}

class DefaultCurrencyDataSource(
    private val currencyService: CurrencyService,
) : CurrencyDataSource {
    override suspend fun allCurrencies(): List<CurrencyResponse> = currencyService
        .allCurrencies()
        .data.map(CurrencyResource::asResponse)

    override suspend fun addFavouriteCurrency(userId: UUID, currencyId: String): Boolean = try {
        transaction {
            FavoriteCurrencies.insert {
                it[FavoriteCurrencies.userId] = userId
                it[FavoriteCurrencies.currencyId] = currencyId
            }
        }
        true
    } catch (e: ExposedSQLException) {
        println("Error adding favorite: ${e.localizedMessage}") // todo add logging
        false
    }

    override suspend fun allUserCurrencies(userId: UUID): List<UserCurrencyResponse>? = try {
        val favouriteCurrenciesIds = transaction {
            FavoriteCurrencies.select { FavoriteCurrencies.userId eq userId }
                .map { it[FavoriteCurrencies.currencyId] }
        }.toSet()

        allCurrencies().map { it.asResponse(favouriteCurrenciesIds) }
    } catch (e: ExposedSQLException) {
        println("Error adding favorite: ${e.localizedMessage}") // todo add logging
        null
    }

    override suspend fun deleteFavouriteCurrency(userId: UUID, currencyId: String): Boolean = try {
        transaction {
            FavoriteCurrencies.deleteWhere {
                (FavoriteCurrencies.userId eq userId) and (FavoriteCurrencies.currencyId eq currencyId)
            }
        }
        true
    } catch (e: ExposedSQLException) {
        println("Error removing favorite: ${e.localizedMessage}")
        false
    }

    override suspend fun getFavouriteCurrencies(userId: UUID): List<CurrencyResponse>? {
        val favouriteCurrenciesIds = try {
            transaction {
                FavoriteCurrencies.select { FavoriteCurrencies.userId eq userId }
                    .map { it[FavoriteCurrencies.currencyId] }
            }.toSet()
        } catch (e: ExposedSQLException) {
            println("Error getting favorite: ${e.localizedMessage}")
            null
        } ?: return null

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

private fun CurrencyResponse.asResponse(favouriteCurrenciesIds: Set<String>): UserCurrencyResponse =
    UserCurrencyResponse(
        id = id,
        name = name,
        symbol = symbol,
        priceUsd = priceUsd,
        isFavourite = id in favouriteCurrenciesIds,
    )
