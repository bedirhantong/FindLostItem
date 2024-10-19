package com.ribuufing.findlostitem

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase'i başlat
        FirebaseApp.initializeApp(this)
    }
}
