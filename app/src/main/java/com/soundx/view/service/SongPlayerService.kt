package com.soundx.view.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SongPlayerService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        TODO("Add Song Player Service")
    }
}