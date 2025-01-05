package com.evandhardspace.yacca.plugins

import com.evandhardspace.yacca.data.user.UserDataSource
import com.evandhardspace.yacca.routes.deleteUser
import com.evandhardspace.yacca.routes.signIn
import com.evandhardspace.yacca.routes.signUp
import com.evandhardspace.yacca.security.hashing.HashingService
import com.evandhardspace.yacca.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
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
        secret()

    }
}

// todo remove
private fun Route.secret() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            call.respond(HttpStatusCode.OK, userId)
        }
    }
}
