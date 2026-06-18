package com.example.esercizioapi.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.esercizioapi.data.AlberoRicerca
import com.example.esercizioapi.data.Repository
import com.example.esercizioapi.network.Backend
import com.example.esercizioapi.network.Infos
import com.example.esercizioapi.network.PokePagingSource
import com.example.esercizioapi.network.UiPokemon
import com.example.esercizioapi.network.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(repository: Repository)
    : ViewModel() {

    private val tag = "HomeViewModel"

    val field : TextFieldState = TextFieldState()

    private val backend : Backend = Backend(repository)

    lateinit var ricerca : AlberoRicerca

    val flowRicercaPokemon = MutableStateFlow<List<UiPokemon>?>(null)
    val stateflow = flowRicercaPokemon.asStateFlow()

    val userPagingFlow : Flow<PagingData<UiPokemon>> = Pager(
        config = PagingConfig(
            pageSize = 7,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            PokePagingSource(backend, 7)
        }
    ).flow.cachedIn(viewModelScope)

    fun initRicerca() {
        viewModelScope.launch(Dispatchers.IO) {
            if (backend.nPokemon == 0)
                backend.getCount()

            try {
                if (backend.info.results.count() != backend.nPokemon) {
                    backend.getContainer(0, backend.nPokemon)
                }
            }
            catch (e: Exception) {
                Log.e(tag, e.message!!)
                backend.getContainer(0, backend.nPokemon)
            }

            when(backend.firstState){
                is UiState.Success -> ricerca = AlberoRicerca(backend.info.results)
                else -> {
                }
            }
        }
    }

    fun searchName(query: String) : List<String> {

        var listName = listOf<String>()
        try{
            val lista = ricerca.dfs(query)
            for(item in lista) {
                listName = listName + (item as Infos).name
            }
        }catch (e : Exception) {
            Log.e(tag, e.message!!)
            listName = listOf("albero non inizializzato")
        }
        return listName
    }

    fun searchInfoPokemons(query : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val out: List<UiPokemon>
            val list = searchName(query)

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

    private var _selectPokemonName = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val retriveInfoPokemon = _selectPokemonName.flatMapLatest {
        flow{
            emit(UiState.Loading)
            try{
                emit(UiState.Success(backend.getInfoPokemon(_selectPokemonName.value)))
            }catch (e : Exception) {
                emit(UiState.Error(e))
            }
        }
    }

    fun setPokemonName(name : String) {
        _selectPokemonName.update {
            name
        }
    }

    val refreshStare = MutableStateFlow(System.currentTimeMillis())

    val retriveFavouritePokemon = refreshStare.flatMapLatest {
        flow{
            emit(UiState.Loading)
            try{
                emit(UiState.Success(backend.repository.getFavouritePokemons()))
            }catch (e : Exception) {
                emit(UiState.Error(e))
            }
        }
    }

    fun insertFavourite(poke : UiPokemon) {
        viewModelScope.launch {
            backend.repository.insertFavouritePokemon(poke)
            refreshStare.update { System.currentTimeMillis() }
        }
    }

    fun removeFavourite(poke : UiPokemon) {
        viewModelScope.launch {
            backend.repository.removeFavouritePokemon(poke)
            refreshStare.update { System.currentTimeMillis() }
        }
    }

    @Composable
    fun CheckAlert() {

        if(backend.firstState is UiState.Error){
            Alert((backend.firstState as UiState.Error).error.message!!)
        }else if (backend.secondState is UiState.Error){
            Alert((backend.secondState as UiState.Error).error.message!!)
        }
    }

    @Composable
    private fun Alert(message: String) {

        Toast.makeText(LocalContext.current, "Errore: $message", Toast.LENGTH_LONG).show()
    }
}