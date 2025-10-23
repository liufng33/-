package com.cleanarch.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CleanArchApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
