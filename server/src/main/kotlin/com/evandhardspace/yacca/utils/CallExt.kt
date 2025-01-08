package com.evandhardspace.yacca.utils

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

val RoutingCall.userId: String?
    get() = principal<JWTPrincipal>()?.getClaim("userId", String::class)
