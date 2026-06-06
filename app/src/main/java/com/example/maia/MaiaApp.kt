package com.example.maia

import android.app.Application
import com.example.maia.util.NotificationHelper

class MaiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
