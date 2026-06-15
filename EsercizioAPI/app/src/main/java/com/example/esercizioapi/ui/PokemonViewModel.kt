package com.example.esercizioapi.ui

import androidx.lifecycle.ViewModel
import com.example.esercizioapi.data.Repository
import com.example.esercizioapi.network.Backend
import com.example.esercizioapi.network.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val repository: Repository)
    : ViewModel() {

    private val TAG = "PokemonViewModel"

    private val backend : Backend = Backend(repository)

    private val _selectPokemonName = MutableStateFlow("")

    val retriveInfoPokemon = _selectPokemonName.flatMapLatest {
        flow{
            emit(UiState.Loading)
            try{
                emit(backend.getInfoPokemon(_selectPokemonName.value))
            }catch (e : Exception) {
                emit(e)
            }
        }
    }

    fun setPokemonName(name : String) {
        _selectPokemonName.update {
            name
        }
    }
}