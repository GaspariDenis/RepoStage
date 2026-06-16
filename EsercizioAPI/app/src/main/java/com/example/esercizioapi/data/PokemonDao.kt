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
}

@Dao
interface ContainerDao {
    @Query("select count(*) from container")
    fun getLength() : Int

    @Query("select * from container")
    fun getContainer() : Container

    @Insert
    fun insertContainer(container: Container)
}

@Dao
interface FavouritePokemonDao {

    @Query("select * from pokemon")
    fun getAll() : List<Pokemon>

    @Insert
    fun insertAll(vararg  pokemons: Pokemon)

    @Delete
    fun deleteAll(vararg pokemons : Pokemon)
}

@Database(entities = [Pokemon::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao() : PokemonDao
}

@Database(entities = [Container::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ContainerDatabase : RoomDatabase() {
    abstract fun pokemonDao() : ContainerDao
}

@Database(entities = [Pokemon::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class FavouritePokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao() : FavouritePokemonDao
}