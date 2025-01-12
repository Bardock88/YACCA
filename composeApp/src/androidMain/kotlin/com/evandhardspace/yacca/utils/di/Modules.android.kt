package com.evandhardspace.yacca.utils.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.evandhardspace.yacca.utils.createDataStore
import com.evandhardspace.yacca.utils.security.provideSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DataStore<Preferences>> { createDataStore(context = get()) }
    single<Settings> { provideSettings(context = get()) }
}