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

sealed interface UiState {

    data class Success(val json : Container) : UiState
    object Loading : UiState
    object Error : UiState
}


sealed interface SecondUiState {
    data class Success(val json : Pokemon) : SecondUiState
    object Loading : SecondUiState
    object Error : SecondUiState
}

class Backend(
    val repository: Repository
){
    private val TAG = "Backend"

    var firstState : UiState by mutableStateOf(UiState.Loading)
        private set

    var secondState : SecondUiState by mutableStateOf(SecondUiState.Loading)
        private set

    lateinit var info : Container
        private set

    lateinit var infoPokemon: Pokemon
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
            val listRes = repository.getRangeInfo(offset, limit)
            info = listRes
            Log.d(TAG,listRes.toString())
            UiState.Success(listRes)
        }catch (e : IOException) {
            Log.e(TAG, e.toString())
            UiState.Error
        }
    }

    suspend fun getPokemon(name : String) {
        secondState = try{
            Log.d(TAG, "Chiesto nome: $name")
            val res = repository.getPokemon(name)
            infoPokemon = res
            Log.d(TAG, res.toString())
            SecondUiState.Success(res)
        }catch (e : IOException) {
            Log.e(TAG, e.toString())
            SecondUiState.Error
        }
    }

    suspend fun getInfoPokemon(name : String) : Pokemon {
        getPokemon(name)

        if (secondState is SecondUiState.Success)
            return infoPokemon

        errorCount -= 1
        return Pokemon("Error", -1, errorCount)
    }

    suspend fun getInfoPokemons(page: Int, itemPerPage : Int): List<Pokemon> {
        var out = listOf<Pokemon>()
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