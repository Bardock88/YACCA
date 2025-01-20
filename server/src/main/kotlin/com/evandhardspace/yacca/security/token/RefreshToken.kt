package com.evandhardspace.yacca.security.token

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class RefreshToken(
    val id: UUID,
    val userId: UUID,
    val token: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime,
)