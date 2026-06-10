package com.example.esercizioapi.data

import com.example.esercizioapi.network.APIService
import com.example.esercizioapi.network.Container
import com.example.esercizioapi.network.Pokemon

private const val TAG = "RaccoltaDati"


class Repository(
    private val api : APIService
){
    suspend fun getRangeInfo(offset: Int, limit: Int) : Container = api.getRangeInfo(offset.toString(), limit.toString())

    suspend fun getPokemon(name : String) : Pokemon = api.getInfoPokemon(name)
}