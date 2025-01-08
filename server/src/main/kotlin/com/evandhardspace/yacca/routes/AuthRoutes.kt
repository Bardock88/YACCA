package com.evandhardspace.yacca.routes

import com.evandhardspace.yacca.data.request.AuthRequest
import com.evandhardspace.yacca.data.response.AuthResponse
import com.evandhardspace.yacca.data.user.User
import com.evandhardspace.yacca.data.user.UserDataSource
import com.evandhardspace.yacca.security.hashing.HashingService
import com.evandhardspace.yacca.security.hashing.SaltedHash
import com.evandhardspace.yacca.security.token.TokenClaim
import com.evandhardspace.yacca.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
) {
    post("signup") {
        val request = runCatching { call.receive<AuthRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.email.isBlank() || request.password.isBlank()
        val isPasswordToShort = request.password.length < 8
        if (areFieldsBlank || isPasswordToShort) {
            call.respond(HttpStatusCode.Conflict, "Field are blank or password is too short")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            id = UUID.randomUUID(),
            email = request.email,
            hashedPassword = saltedHash.hash,
            salt = saltedHash.salt,
        )

        val userWasInserted = userDataSource.insertUser(user)
        if (!userWasInserted) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
) {
    post("signin") {
        val request = runCatching { call.receive<AuthRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUser(request.email)

        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                user.hashedPassword,
                user.salt,
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            TokenClaim(
                name = "userId",
                value = user.id.toString(),
            ),
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token,
            )
        )
    }
}
