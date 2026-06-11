package com.example.esercizioapi

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.esercizioapi.data.Repository
import com.example.esercizioapi.network.Pokemon
import kotlinx.coroutines.launch
import  androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.esercizioapi.data.AlberoRicerca
import com.example.esercizioapi.network.Infos
import com.example.esercizioapi.network.PokePagingSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.esercizioapi.network.Backend
import com.example.esercizioapi.network.UiState
import okhttp3.internal.wait

class PokeViewModel(private val repository: Repository, val query: String) : ViewModel() {
    private val TAG = "ViewModel"

    val field : TextFieldState = TextFieldState()

    private val backend : Backend = Backend(repository)

    lateinit var ricerca : AlberoRicerca

    val flowRicercaPokemon = MutableStateFlow<List<Pokemon>?>(null)
    val stateflow = flowRicercaPokemon.asStateFlow()

    val userPagingFlow : Flow<PagingData<Pokemon>> = Pager(
        config = PagingConfig(
            pageSize = 7,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            PokePagingSource(backend, 7, query)
        }
    ).flow
        .cachedIn(viewModelScope)

    init {
        initRicerca()
    }

    private fun initRicerca() {
        viewModelScope.launch(Dispatchers.IO) {
            if(backend.nPokemon == 0)
                backend.getCount()

            try{
                if(backend.info.results.count() != backend.nPokemon){
                    backend.getContainer(0, backend.nPokemon)
                }
            }catch (e : Exception) {
                backend.getContainer(0, backend.nPokemon)
            }


            if(backend.firstState is UiState.Success){
                ricerca = AlberoRicerca(backend.info.results)
            }
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

    fun getInfoPokemons(name : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val out: List<Pokemon>
            val list = searchName(name)

            var jobs = listOf<Deferred<Pokemon>>()

            out = buildList {
                for (n in list) {
                    add(async {
                        backend.getInfoPokemon(n)
                    })
                }
            }.awaitAll()

            flowRicercaPokemon.value = out
        }
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