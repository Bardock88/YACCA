package com.evandhardspace.yacca.data.datasources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val IS_USER_LOGGED_KEY = "is_user_logged"

internal interface UserDataSource {
    fun isUserLoggedIn(): Flow<Boolean>
    suspend fun setUserLogged(isUserLogged: Boolean)
    suspend fun clear()
}

class LocalUserDataSource(
    private val dataStore: DataStore<Preferences>,
) : UserDataSource {

    override fun isUserLoggedIn(): Flow<Boolean> = dataStore.data.map {
        it[booleanPreferencesKey(IS_USER_LOGGED_KEY)] ?: false
    }

    override suspend fun setUserLogged(isUserLogged: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(IS_USER_LOGGED_KEY)] = isUserLogged
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

