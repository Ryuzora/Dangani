package com.ryuzora.dangani

import android.app.Application
import com.ryuzora.dangani.data.local.DanganiDatabase

class DanganiApplication : Application() {

    lateinit var database: DanganiDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = DanganiDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: DanganiApplication
            private set
    }
}

