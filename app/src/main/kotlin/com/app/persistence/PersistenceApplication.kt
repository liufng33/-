package com.app.persistence

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PersistenceApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
