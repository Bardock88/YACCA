package com.evandhardspace.yacca.routes

import com.evandhardspace.yacca.data.currency.CurrencyDataSource
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.currencies(
    currencyDataSource: CurrencyDataSource,
) {
    get("currencies") {
        val currencies = currencyDataSource.allCurrencies()
        call.respond(HttpStatusCode.OK, currencies)
    }
}
