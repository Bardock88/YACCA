package com.evandhardspace.yacca.routes

import com.evandhardspace.yacca.Endpoints
import com.evandhardspace.yacca.data.user.UserDataSource
import com.evandhardspace.yacca.utils.userId
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.deleteUser(
    userDataSource: UserDataSource,
) {
    authenticate {
        delete(Endpoints.USER) {
            val userId = call.userId
            val isUserDeleted = userDataSource.deleteUser(UUID.fromString(userId))

            if(isUserDeleted.not()) {
                call.respond(HttpStatusCode.Conflict, "User was not deleted.")
                return@delete
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}
