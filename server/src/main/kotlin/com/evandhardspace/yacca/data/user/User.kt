package com.evandhardspace.yacca.data.user

import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val hashedPassword: String,
    val salt: String,
)
