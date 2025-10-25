package com.yingshi.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YingshiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
