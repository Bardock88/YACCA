package com.evandhardspace.yacca.domain

import com.evandhardspace.yacca.data.datasources.LocalCurrenciesDataSource
import com.evandhardspace.yacca.data.datasources.TokenDataSource
import com.evandhardspace.yacca.data.datasources.UserDataSource
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

internal class CleanUpManager(
    private val userRepository: UserDataSource,
    private val tokenDataSource: TokenDataSource,
    private val localCurrenciesDataSource: LocalCurrenciesDataSource,
) {

    suspend fun clear() = withContext(NonCancellable) {
        tokenDataSource.clear()
        userRepository.clear()
        localCurrenciesDataSource.clear()
    } // todo try to inject as set
}