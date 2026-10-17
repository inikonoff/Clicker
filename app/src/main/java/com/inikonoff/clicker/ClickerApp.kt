package com.inikonoff.clicker

import android.app.Application

class ClickerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CounterRepository.init(this)
    }
}
