package com.sourcemanager

import android.app.Application
import com.sourcemanager.di.AppContainer

class SourceManagerApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}
