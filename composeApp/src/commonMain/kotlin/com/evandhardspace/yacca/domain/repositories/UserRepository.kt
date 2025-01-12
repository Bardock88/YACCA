package com.evandhardspace.yacca.domain.repositories

import com.evandhardspace.yacca.data.datasources.UserDataSource
import kotlinx.coroutines.flow.Flow

internal class UserRepository(
    private val userDataSource: UserDataSource,
): Cleanable {

    fun isUserLoggedIn(): Flow<Boolean> =
        userDataSource.isUserLoggedIn()

    suspend fun setUserLogged(isUserLogged: Boolean) =
        userDataSource.setUserLogged(isUserLogged)

    override suspend fun clear() {
        setUserLogged(false)
    }
}