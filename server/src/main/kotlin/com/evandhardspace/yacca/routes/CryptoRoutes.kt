package com.evandhardspace.yacca.routes

import com.evandhardspace.yacca.Endpoints
import com.evandhardspace.yacca.data.currency.CurrencyDataSource
import com.evandhardspace.yacca.request.FavouriteCurrencyRequest
import com.evandhardspace.yacca.utils.userId
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.currencies(
    currencyDataSource: CurrencyDataSource,
) {
    get(Endpoints.CURRENCIES_PUBLIC_INFO) {
        val currencies = currencyDataSource.allCurrencies()
        call.respond(HttpStatusCode.OK, currencies)
    }

    authenticate {
        get(Endpoints.CURRENCIES_USER_INFO) {
            val userId = call.userId ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val favouriteCurrencies = currencyDataSource.allUserCurrencies(UUID.fromString(userId)) ?: run {
                call.respond(HttpStatusCode.Conflict) // todo check if status is correct
                return@get
            }
            call.respond(HttpStatusCode.OK, favouriteCurrencies)
        }

        get(Endpoints.FAVOURITES) {
            val userId = call.userId ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val favouriteCurrencies = currencyDataSource.getFavouriteCurrencies(UUID.fromString(userId)) ?: run {
                call.respond(HttpStatusCode.Conflict) // todo check if status is correct
                return@get
            }
            call.respond(HttpStatusCode.OK, favouriteCurrencies)
        }

        post(Endpoints.FAVOURITES) {
            val request = runCatching { call.receive<FavouriteCurrencyRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val isAddedSuccessfully = currencyDataSource.addFavouriteCurrency(UUID.fromString(userId), request.id)

            if(isAddedSuccessfully.not()) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }

        delete("${Endpoints.FAVOURITES}/{id}") {
            val favouriteCurrencyId = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val userId = call.userId ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val isDeletedSuccessfully = currencyDataSource.deleteFavouriteCurrency(UUID.fromString(userId), favouriteCurrencyId)

            if(isDeletedSuccessfully.not()) {
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}
