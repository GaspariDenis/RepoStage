package com.example.esercizioapi

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.esercizioapi.data.Repository
import com.example.esercizioapi.network.Container
import com.example.esercizioapi.network.Pokemon
import kotlinx.coroutines.launch
import java.io.IOException
import  androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.esercizioapi.data.AlberoRicerca
import com.example.esercizioapi.network.Infos
import com.example.esercizioapi.network.Sprite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PokeViewModel(private val repository: Repository) : ViewModel() {
    private val TAG = "ViewModel"

    var state : UiState by mutableStateOf(UiState.Loading)
        private set

    var secondState : SecondUiState by mutableStateOf(SecondUiState.Loading)
        private set

    lateinit var info : Container
        private set

    lateinit var infoPokemon: Pokemon
        private set

    private suspend fun GETinfo(offset: Int, limit : Int) {
        state = try{
            val listRes = repository.getRangeInfo(offset, limit)
            info = listRes
            Log.d(TAG,listRes.toString())
            UiState.Success(listRes)
        }catch (e : IOException) {
            UiState.Error
        }
    }

    private suspend fun GETInfoPokemon(name : String) {
        secondState = try{
            Log.d(TAG, "Chiesto nome: $name")
            val res = repository.getPokemon(name)
            infoPokemon = res
            Log.d(TAG, res.toString())
            SecondUiState.Success(res)
        }catch (e : IOException) {
            SecondUiState.Error
        }
    }

    init {
        InitRicerca()
    }

    var NPokemon : Int = 0

    lateinit var Ricerca : AlberoRicerca

    private suspend fun GetCount() {
        GETinfo(0,0)
        if(state is UiState.Success){
            NPokemon = info.count
        }
    }

    private fun InitRicerca() {
        Log.d(TAG, "Inizio Init")
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO){
                if(NPokemon == 0)
                    GetCount()
            }

            Log.d(TAG, NPokemon.toString())
            withContext(Dispatchers.IO){
                try{
                    if(info.results.count() != NPokemon)
                        GETinfo(0, NPokemon)
                }catch (e : Exception) {
                    GETinfo(0, NPokemon)
                }
            }

            if(state is UiState.Success){
                Ricerca = AlberoRicerca(info.results)
            }
        }
    }

    fun searchListName(name: String) : List<String> {
        var listName = listOf<String>()
        try{
            val lista = Ricerca.DFS(name)
            for(item in lista) {
                listName = listName + (item as Infos).name
            }
        }catch (e : Exception) {
            listName = listOf("Nessun pokemon trovato")
        }
        return listName
    }

    private suspend fun getInfoPokemon(name : String) : Pokemon {
        GETInfoPokemon(name)

        if(secondState is SecondUiState.Success)
            return infoPokemon
        return Pokemon("Error", 0,0, Sprite(""))
    }

    val flow = MutableStateFlow<List<Pokemon>?>(null)
    val stateflow = flow.asStateFlow()

    fun getInfoPokemons(name : String) {
        viewModelScope.launch(Dispatchers.IO) {
            var out = listOf<Pokemon>()
            val list = searchListName(name)

            for(n in list) {
                withContext(Dispatchers.IO){
                    out = out + getInfoPokemon(n)
                }
            }

            flow.value = out
        }
    }

    suspend fun getInfoPokemons(page: Int, itemPerPage : Int): List<Pokemon> {
        var out = listOf<Pokemon>()
        GETinfo(page, itemPerPage)

        for(n in info.results) {
            withContext(Dispatchers.IO){
                out = out + getInfoPokemon(n.name)
            }
        }
        return out
    }

    companion object{
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =(this[APPLICATION_KEY] as application)
                val repo = application.containter.repository
                PokeViewModel(repo)
            }
        }
    }
}


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