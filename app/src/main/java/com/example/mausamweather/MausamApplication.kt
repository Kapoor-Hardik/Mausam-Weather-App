package com.example.mausamweather

import android.app.Application
import com.example.mausamweather.di.AppContainer

class MausamApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
