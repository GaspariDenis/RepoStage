package com.example.esercizioapi.ui

import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
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
import com.example.esercizioapi.network.Pokemon
import com.example.esercizioapi.network.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
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
class HomeViewModel @Inject constructor(private val repository: Repository)
    : ViewModel() {

    private val TAG = "HomeViewModel"

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
            PokePagingSource(backend, 7, "")
        }
    ).flow
        .cachedIn(viewModelScope)

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
            listName = listOf("albero non inizializzato")
        }
        return listName
    }

    fun searchInfoPokemons(query : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val out: List<Pokemon>
            val list = searchName(query)

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

    private var _selectPokemonName = MutableStateFlow("")

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

    val isFavourite = _selectPokemonName.flatMapLatest {
        flow{
            emit(UiState.Loading)
            try{
                emit(UiState.Success(backend.repository.getFavouritePokemon(_selectPokemonName.value).name == _selectPokemonName.value))
            }catch (e : Exception){
                emit(UiState.Error(e))
            }
        }
    }

    fun setPokemonName(name : String) {
        _selectPokemonName.update {
            name
        }
    }

    val retriveFavouritePokemon = flow{
        emit(UiState.Loading)
        try{
            emit(UiState.Success(backend.repository.getFavouritePokemons()))
        }catch (e : Exception) {
            emit(UiState.Error(e))
        }
    }


    fun insertFavourite(poke : Pokemon) {
        viewModelScope.launch {
            backend.repository.insertFavouritePokemon(poke)
        }
    }

    fun removeFavourite(poke : Pokemon) {
        viewModelScope.launch {
            backend.repository.removeFavouritePokemon(poke)
        }
    }

    @Composable
    fun checkAlert() {

        if(backend.firstState is UiState.Error){
            alert((backend.firstState as UiState.Error).error.message!!)
        }else if (backend.secondState is UiState.Error){
            alert((backend.secondState as UiState.Error).error.message!!)
        }
    }

    @Composable
    private fun alert(message: String) {

        Toast.makeText(LocalContext.current, "Errore: $message", Toast.LENGTH_LONG).show()
    }
}