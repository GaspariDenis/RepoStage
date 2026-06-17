package com.example.esercizioapi.network

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.esercizioapi.data.Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.IOException

sealed interface UiState<out T>{
    data class Success<T>(val json : T) : UiState<T>
    object Loading : UiState<Nothing>
    data class Error(val error: Throwable) : UiState<Nothing>
}

class Backend(
    val repository: Repository
){
    private val TAG = "Backend"

    var firstState : UiState<Container> by mutableStateOf(UiState.Loading)
        private set

    var secondState : UiState<UiPokemon> by mutableStateOf(UiState.Loading)
        private set

    lateinit var info : Container
        private set

    lateinit var infoPokemon: UiPokemon
        private set

    var nPokemon : Int = 0
        private set

    private var errorCount = 0

    suspend fun getCount() {
        getContainer(0,0)
        if(firstState is UiState.Success){
            nPokemon = info.count
        }
    }

    suspend fun getContainer(offset: Int, limit : Int) {
        firstState = try{
            val listRes : Container
            if(offset == 0 && limit == 0){
                listRes = repository.getContainer(offset, limit)
            }else{
                listRes = repository.getRangePokemon(offset, limit)
            }
            info = listRes
            Log.d(TAG,listRes.toString())
            UiState.Success(listRes)
        }catch (e : IOException) {
            Log.e(TAG, e.toString())
            UiState.Error(Throwable(e.message, e.cause))
        }
    }

    suspend fun getPokemon(name : String) {
        secondState = try{
            Log.d(TAG, "Chiesto nome: $name")
            val res = repository.getPokemon(name)
            infoPokemon = res
            Log.d(TAG, res.toString())
            UiState.Success(res)
        }catch (e : IOException) {
            Log.e(TAG, e.toString())
            UiState.Error(Throwable(e.message, e.cause))
        }
    }

    suspend fun getInfoPokemon(name : String) : UiPokemon {
        getPokemon(name)

        if (secondState is UiState.Success)
            return infoPokemon

        errorCount -= 1
        return UiPokemon("Error", -1, errorCount)
    }

    suspend fun getInfoPokemons(page: Int, itemPerPage : Int): List<UiPokemon> {
        var out = listOf<UiPokemon>()
        getContainer(page, itemPerPage)

        if(firstState is UiState.Success){

            coroutineScope {
                out = buildList {
                    for(n in info.results){
                        add(async {
                            getInfoPokemon(n.name)
                        })
                    }
                }.awaitAll()
            }

            return out
        }

        return listOf()
    }
}