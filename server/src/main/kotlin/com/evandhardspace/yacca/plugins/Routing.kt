package com.evandhardspace.yacca.plugins

import com.evandhardspace.yacca.data.currency.CurrencyDataSource
import com.evandhardspace.yacca.data.user.UserDataSource
import com.evandhardspace.yacca.routes.currencies
import com.evandhardspace.yacca.routes.deleteUser
import com.evandhardspace.yacca.routes.signIn
import com.evandhardspace.yacca.routes.signUp
import com.evandhardspace.yacca.security.hashing.HashingService
import com.evandhardspace.yacca.security.token.TokenService
import com.evandhardspace.yacca.utils.userId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    tokenService: TokenService,
    userDataSource: UserDataSource,
    currencyDataSource: CurrencyDataSource,
) {
    routing {
        get("/") {
            call.respondText("Hello YACCA!")
        }
        signUp(
            hashingService = hashingService,
            userDataSource = userDataSource,
        )
        signIn(
            userDataSource = userDataSource,
            hashingService = hashingService,
            tokenService = tokenService,
        )
        deleteUser(
            userDataSource = userDataSource,
        )
        currencies(
            currencyDataSource = currencyDataSource,
        )
        secret()
    }
}

// todo remove
private fun Route.secret() {
    authenticate {
        get("secret") {
            val userId = call.userId ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            call.respond(HttpStatusCode.OK, userId)
        }
    }
}
