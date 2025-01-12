package com.evandhardspace.yacca

import android.app.Application
import com.evandhardspace.yacca.utils.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class YaccaApp: Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@YaccaApp)
            modules(appModule)
        }
    }
}
