package com.evandhardspace.yacca.routes

import com.evandhardspace.yacca.data.user.UserDataSource
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.deleteUser(
    userDataSource: UserDataSource,
) {
    authenticate {
        delete("user") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val isUserDeleted = userDataSource.deleteUser(UUID.fromString(userId))

            if(isUserDeleted.not()) {
                call.respond(HttpStatusCode.Conflict, "User was not deleted.")
                return@delete
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}
