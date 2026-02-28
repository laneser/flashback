package com.flashback.app

import android.app.Application
import com.flashback.app.service.MonitoringNotification

class FlashbackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MonitoringNotification.createChannel(this)
    }
}
