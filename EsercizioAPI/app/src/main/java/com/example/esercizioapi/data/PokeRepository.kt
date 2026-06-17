package com.example.esercizioapi.data

import android.util.Log
import androidx.room.Room
import com.example.esercizioapi.Application
import com.example.esercizioapi.network.APIService
import com.example.esercizioapi.network.Container
import com.example.esercizioapi.network.Infos
import com.example.esercizioapi.network.Pokemon
import com.example.esercizioapi.network.Favourite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "RaccoltaDati"


class Repository @Inject constructor(
    val api : APIService,
) {
    val db = Room.databaseBuilder(
        Application.appContext,
        PokemonDatabase::class.java, "pokemon"
    ).build()

    val pokemonDao = db.pokemonDao()

    suspend fun getContainer(offset: Int, limit: Int) : Container{
        try {
            val cont : Container
            withContext(Dispatchers.Default){
                cont = pokemonDao.getContainer()
            }
            return cont
        }catch(e : Exception){
            Log.e(TAG, e.message!!)
            val cont = api.getRangeInfo(offset.toString(), limit.toString())
            withContext(Dispatchers.Default){
                if(cont.next == null)
                    pokemonDao.insertContainer(cont)
            }
            return cont
        }
    }

    suspend fun getPokemon(name : String) : Pokemon {
        try{
            Log.d(TAG, "Cerco $name nel database locale.")
            val poke : Pokemon
            withContext(Dispatchers.Default){
                poke = pokemonDao.getPokemon(name)
            }
            return poke
        }catch (e : Exception) {
            Log.e(TAG, e.message!!)
            Log.w(TAG, "Non è stato trovato nel database, inizio chiamata...")
            val poke = api.getInfoPokemon(name)
            Log.d(TAG, "Inserito nel database $poke")
            withContext(Dispatchers.Default){
                pokemonDao.insertAll(poke)
            }
            return poke
        }
    }

    suspend fun getRangePokemon(offset: Int, limit: Int) : Container {
        try{
            val pokes : List<Pokemon>
            withContext(Dispatchers.Default){
                pokes = pokemonDao.getRangePokemon(offset, limit)

                if(pokes.isEmpty())
                    throw Exception("Database vuoto con offset $offset e limit $limit...")

                var i = 1
                pokes.forEach { item ->
                    if(item.id != offset + i)
                        throw Exception("ID sbagliato")
                    i += 1
                }
            }
            return ConvertPokemonsToContainer(pokes)
        }catch (e : Exception){
            Log.e(TAG, e.message!!)
            return api.getRangeInfo(offset.toString(), limit.toString())
        }
    }

    suspend fun getFavouritePokemons() : List<Pokemon> {
        val list : List<Pokemon>
        withContext(Dispatchers.Default){
            list = pokemonDao.getAll()
        }
        return list
    }

    suspend fun getFavouritePokemon(name: String) : Pokemon {
        val poke : Pokemon
        withContext(Dispatchers.Default) {
            poke = pokemonDao.getFavourite(name)
        }
        return poke
    }

    suspend fun insertFavouritePokemon(pokemons : Pokemon) {
        withContext(Dispatchers.Default) {
            pokemonDao.insertFavourite(Favourite(pokemons.name))
        }
    }

    suspend fun removeFavouritePokemon(pokemons : Pokemon) {
        withContext(Dispatchers.Default) {
            pokemonDao.deleteFavourite(Favourite(pokemons.name))
        }
    }
}

private fun ConvertPokemonsToContainer(list : List<Pokemon>) : Container {
    var infoList : List<Infos> = listOf()

    list.forEach { item->
        infoList = infoList + Infos(item.name, "https://pokeapi.co/api/v2/pokemon/${item.id}/")
    }

    return Container(0, null, null, infoList)
}