package com.timmy.securitiesexam.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: App
    }

}