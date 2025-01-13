package com.evandhardspace.yacca.domain

import com.evandhardspace.yacca.domain.repositories.AuthRepository
import com.evandhardspace.yacca.domain.repositories.CurrencyRepository
import com.evandhardspace.yacca.domain.repositories.UserRepository

internal class CleanUpManager(
    private val authRepository: AuthRepository,
    private val currencyRepository: CurrencyRepository,
    private val userRepository: UserRepository,
) {

    suspend fun clear() {
        authRepository.clear()
        currencyRepository.clear()
        userRepository.clear()
    } // todo try to inject as set
}