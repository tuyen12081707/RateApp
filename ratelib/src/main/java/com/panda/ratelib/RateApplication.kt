package com.panda.ratelib

import android.app.Application
import com.google.firebase.FirebaseApp

class RateApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}