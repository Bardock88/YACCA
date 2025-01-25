package com.evandhardspace.yacca.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.evandhardspace.yacca.utils.safeSystemGetEnv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val secretKey = safeSystemGetEnv("JWT_SECRET", "mock_jwt_secret_key")
    val audience = environment.config.property("jwt.audience").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secretKey))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(audience)) JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}
