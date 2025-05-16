package com.example.lupath

import android.app.Application
import com.example.lupath.data.database.AppDatabase// <<< Make sure this path is correct
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    // Using 'by lazy' so the database is only created when it's first accessed,
    // not when the Application class itself is instantiated.
    // This is good for performance.
//    val database: AppDatabase by lazy {
//        AppDatabase.getDatabase(this) // 'this' refers to the Application context
//    }

    override fun onCreate() {
        super.onCreate()
        // You can perform other application-wide initializations here if needed
        // For example, initializing logging libraries, analytics, etc.
        // The database will be initialized on its first access due to 'by lazy'.
    }
}