package com.evandhardspace.yacca.utils.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.evandhardspace.yacca.data.database.AppDatabase
import com.evandhardspace.yacca.data.database.getDatabaseBuilder
import com.evandhardspace.yacca.utils.NetworkMonitor
import com.evandhardspace.yacca.utils.createDataStore
import com.evandhardspace.yacca.utils.createNetworkMonitor
import com.evandhardspace.yacca.utils.security.provideSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DataStore<Preferences>> { createDataStore() }
    single<Settings> { provideSettings() }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
    single<NetworkMonitor> { createNetworkMonitor() }
}