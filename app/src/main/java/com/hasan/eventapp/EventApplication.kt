package com.hasan.eventapp

import android.app.Application
import com.hasan.eventapp.data.managers.MockDataManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class EventApplication : Application(){
    private val mockDataManager by lazy { MockDataManager(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        // Launch in a coroutine scope
        CoroutineScope(Dispatchers.IO).launch {
            mockDataManager.initializeDataIfNeeded()
        }
    }
}