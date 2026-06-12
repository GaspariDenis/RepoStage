package com.example.esercizioapi.data

import com.example.esercizioapi.network.APIService
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AppContainer @Inject constructor(){
    private val BASE_URL = "https://pokeapi.co/api/v2/"

    val repository : Repository by lazy {
        Repository(retrofitService)
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService : APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}