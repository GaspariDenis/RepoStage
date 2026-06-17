package com.example.esercizioapi.data

import android.R
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.esercizioapi.network.Container
import com.example.esercizioapi.network.Favourite
import com.example.esercizioapi.network.Pokemon

@Dao
interface PokemonDao {
    //@Query("select * from container")
    //fun getAll() : Container

    @Query("select * from pokemon where name like (:name) limit 1")
    fun getPokemon(name : String) : Pokemon

    @Query("select * from pokemon where id > (:offset) order by id asc limit (:limit)")
    fun getRangePokemon(offset: Int, limit: Int) : List<Pokemon>

    @Insert
    fun insertAll(vararg  pokemons: Pokemon)

    @Query("select count(*) from container")
    fun getLength() : Int

    @Query("select * from container")
    fun getContainer() : Container

    @Insert
    fun insertContainer(container: Container)

    @Query("select * from pokemon inner join favourite on(name=PokeName)")
    fun getAll() : List<Pokemon>

    @Query("select * from pokemon inner join favourite on(name=PokeName) where name=(:name)")
    fun getFavourite(name : String) : Pokemon

    @Insert
    fun insertFavourite(name: Favourite)

    @Delete
    fun deleteFavourite(name: Favourite)
}


@Database(entities = [Pokemon::class, Container::class, Favourite::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao() : PokemonDao
}