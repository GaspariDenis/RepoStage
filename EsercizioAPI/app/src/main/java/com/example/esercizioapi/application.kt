package com.example.esercizioapi

import android.app.Application
import com.example.esercizioapi.data.AppContainer

class application : Application() {
    lateinit var containter : AppContainer
    override fun onCreate() {
        super.onCreate()
        containter = AppContainer()
    }
}