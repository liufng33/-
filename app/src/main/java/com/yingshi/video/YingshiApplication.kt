package com.yingshi.video

import android.app.Application
import com.yingshi.video.data.local.seeding.DataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class YingshiApplication : Application() {

    @Inject
    lateinit var dataSeeder: DataSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        applicationScope.launch {
            dataSeeder.seedInitialData()
        }
    }
}
