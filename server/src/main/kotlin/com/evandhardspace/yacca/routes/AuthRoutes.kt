package com.evandhardspace.yacca.routes

import com.evandhardspace.yacca.Endpoints
import com.evandhardspace.yacca.request.AuthRequest
import com.evandhardspace.yacca.response.AuthResponse
import com.evandhardspace.yacca.data.user.User
import com.evandhardspace.yacca.data.user.UserDataSource
import com.evandhardspace.yacca.response.RefreshRequest
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
    post(Endpoints.SIGNUP) {
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
            call.respond(HttpStatusCode.Conflict, "User was not added to db")
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
    post(Endpoints.SIGNIN) {
        val request = runCatching { call.receive<AuthRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUser(request.email)
        if (user == null || !hashingService.verify(request.password, SaltedHash(user.hashedPassword, user.salt))) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val accessToken = tokenService.generateAccessToken(TokenClaim("userId", user.id.toString()))
        val refreshToken = tokenService.generateRefreshToken(user.id)

        tokenService.storeRefreshToken(
            user.id.toString(),
            refreshToken = refreshToken,
        )

        call.respond(
            HttpStatusCode.OK,
            message = AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken.token,
            )
        )
    }
}

fun Route.refreshToken(
    tokenService: TokenService,
) {
    post(Endpoints.REFRESH) {
        val request = runCatching { call.receive<RefreshRequest>() }.getOrElse {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val decodedToken = tokenService.verifyRefreshToken(request.refreshToken)
        if (decodedToken == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
            return@post
        }
        val userId = decodedToken.getClaim("userId").asString()

        val newAccessToken = tokenService.generateAccessToken(TokenClaim("userId", userId))
        val newRefreshToken = tokenService.rotateRefreshToken(request.refreshToken)

        if(newRefreshToken == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid or expired refresh token")
            return@post
        }

        call.respond(
            HttpStatusCode.OK,
            message = AuthResponse(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken.token,
            )
        )
    }
}
