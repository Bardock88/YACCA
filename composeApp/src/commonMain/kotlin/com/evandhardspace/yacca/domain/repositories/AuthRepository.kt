package com.evandhardspace.yacca.domain.repositories

import kotlinx.coroutines.delay

internal class AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        delay(1000)
    }


    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        delay(1000)
    }
}