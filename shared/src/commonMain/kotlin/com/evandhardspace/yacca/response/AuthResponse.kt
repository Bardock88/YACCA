package com.evandhardspace.yacca.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
)
