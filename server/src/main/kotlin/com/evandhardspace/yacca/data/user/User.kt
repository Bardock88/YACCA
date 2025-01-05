package com.evandhardspace.yacca.data.user

import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: String,
    val salt: String,
)
