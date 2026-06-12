package com.example.esercizioapi

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esercizioapi.data.Repository
import com.example.esercizioapi.network.Pokemon
import kotlinx.coroutines.launch
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.esercizioapi.network.Backend
import com.example.esercizioapi.network.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = PokeViewModel.Factory::class)
class PokeViewModel @AssistedInject constructor(@Assisted private val repository: Repository)
    : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(repository: Repository): PokeViewModel
    }

    private val TAG = "ViewModel"

    val field : TextFieldState = TextFieldState()

    private val backend : Backend = Backend(repository)

    private var show : Boolean = true

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

            when(backend.firstState){
                is UiState.Success -> ricerca = AlberoRicerca(backend.info.results)
                else -> {}
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

    @Composable
    fun checkAlert() {
        if(backend.firstState is UiState.Error){
            alert((backend.firstState as UiState.Error).error.message!!)
        }else if (backend.secondState is UiState.Error){
            alert((backend.secondState as UiState.Error).error.message!!)
        }else {
            show = true
        }
    }

    @Composable
    private fun alert(message: String) {

        if(!show)
            return

        AlertDialog(
            icon = {},
            title = {
                Text(
                    text = "Error"
                )
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        show = false
                    }
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {},
            onDismissRequest = {},
        )
    }
}