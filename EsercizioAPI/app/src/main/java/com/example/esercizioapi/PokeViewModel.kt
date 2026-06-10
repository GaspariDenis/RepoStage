package com.example.esercizioapi

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.esercizioapi.data.AlberoRicerca
import com.example.esercizioapi.network.Infos
import com.example.esercizioapi.network.PokePagingSource
import com.example.esercizioapi.network.Sprite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PokeViewModel(private val repository: Repository, val query: String) : ViewModel() {
    private val TAG = "ViewModel"

    val field : TextFieldState = TextFieldState()

    var firstState : UiState by mutableStateOf(UiState.Loading)
        private set

    var secondState : SecondUiState by mutableStateOf(SecondUiState.Loading)
        private set

    lateinit var info : Container
        private set

    lateinit var infoPokemon: Pokemon
        private set

    var nPokemon : Int = 0

    lateinit var ricerca : AlberoRicerca

    val flowRicercaPokemon = MutableStateFlow<List<Pokemon>?>(null)
    val stateflow = flowRicercaPokemon.asStateFlow()

    val userPagingFlow : Flow<PagingData<Pokemon>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            PokePagingSource(this, 20, query)
        }
    ).flow
        .cachedIn(viewModelScope)

    init {
        initRicerca()
    }

    private fun initRicerca() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO){
                if(nPokemon == 0)
                    getCount()
            }

            withContext(Dispatchers.IO){
                try{
                    if(info.results.count() != nPokemon)
                        getContainer(0, nPokemon)
                }catch (e : Exception) {
                    getContainer(0, nPokemon)
                }
            }

            if(firstState is UiState.Success){
                ricerca = AlberoRicerca(info.results)
            }
        }
    }

    private suspend fun getContainer(offset: Int, limit : Int) {
        firstState = try{
            val listRes = repository.getRangeInfo(offset, limit)
            info = listRes
            Log.d(TAG,listRes.toString())
            UiState.Success(listRes)
        }catch (e : IOException) {
            UiState.Error
        }
    }

    private suspend fun getPokemon(name : String) {
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

    private suspend fun getCount() {
        getContainer(0,0)
        if(firstState is UiState.Success){
            nPokemon = info.count
        }
    }

    fun searchName(name: String) : List<String> {
        var listName = listOf<String>()
        try{
            val lista = ricerca.dfs(name)
            for(item in lista) {
                listName = listName + (item as Infos).name
            }
        }catch (e : Exception) {
            listName = listOf("Nessun pokemon trovato")
        }
        return listName
    }

    private suspend fun getInfoPokemon(name : String) : Pokemon {
        getPokemon(name)

        if (secondState is SecondUiState.Success)
            return infoPokemon
        return Pokemon("Error", 0, 0, Sprite(""))
    }

    fun getInfoPokemons(name : String) {
        viewModelScope.launch(Dispatchers.IO) {
            var out = listOf<Pokemon>()
            val list = searchName(name)

            for(n in list) {
                withContext(Dispatchers.IO){
                    out = out + getInfoPokemon(n)
                }
            }

            flowRicercaPokemon.value = out
        }
    }

    suspend fun getInfoPokemons(page: Int, itemPerPage : Int): List<Pokemon> {
        var out = listOf<Pokemon>()
        getContainer(page, itemPerPage)

        if(firstState is UiState.Success){
            for(n in info.results) {
                withContext(Dispatchers.IO){
                    out = out + getInfoPokemon(n.name)
                }
            }
            return out
        }else if(firstState is UiState.Error){
            initRicerca()
        }

        return listOf()
    }

    companion object{
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =(this[APPLICATION_KEY] as application)
                val repo = application.containter.repository
                PokeViewModel(repo, "")
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