package com.example.masterai

import android.app.Application
import com.google.firebase.FirebaseApp

class MasterAiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
