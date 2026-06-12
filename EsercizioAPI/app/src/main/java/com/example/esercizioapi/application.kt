package com.example.esercizioapi

import android.app.Application
import com.example.esercizioapi.data.AppContainer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class application : Application() {
    @Inject
    lateinit var containter : AppContainer

    override fun onCreate() {
        super.onCreate()
        containter = AppContainer()
    }
}