package com.example.esercizioapi.network

import retrofit2.Retrofit
import retrofit2.http.GET
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

interface APIService {

    @GET("pokemon/?")
    suspend fun getRangeInfo(
        @Query("offset") offset : String,
        @Query("limit") limit : String
        ) : Container

    @GET("pokemon/{id}")
    suspend fun getInfoPokemon(
        @Path("id") name : String
    ) : NetPokemon
}

@Module
@InstallIn(SingletonComponent::class)
object NetModule {
    @Provides
    @Singleton
    fun retrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun api(retrofit: Retrofit): APIService =
        retrofit.create(APIService::class.java)
}