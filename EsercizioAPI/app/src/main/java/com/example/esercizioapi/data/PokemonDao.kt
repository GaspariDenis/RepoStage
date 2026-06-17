package com.example.esercizioapi.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.esercizioapi.network.Container
import com.example.esercizioapi.network.DbPokemon
import com.example.esercizioapi.network.UiPokemon

@Dao
interface PokemonDao {
    @Query("select * from pokemon where name like (:name) limit 1")
    fun getPokemon(name : String) : DbPokemon

    @Query("select * from pokemon where id > (:offset) order by id asc limit (:limit)")
    fun getRangePokemon(offset: Int, limit: Int) : List<DbPokemon>

    @Insert
    fun insertAll(vararg  pokemons: DbPokemon)

    @Query("select count(*) from container")
    fun getLength() : Int

    @Query("select * from container")
    fun getContainer() : Container

    @Insert
    fun insertContainer(container: Container)

    @Query("select * from pokemon inner join favourite on(pokemon.name=favourite.name)")
    fun getAll() : List<UiPokemon>

    @Query("select * from pokemon inner join favourite on(pokemon.name=favourite.name) where pokemon.name=(:name)")
    fun getFavourite(name : String) : UiPokemon

    @Insert
    fun insertFavourite(name: UiPokemon)

    @Delete
    fun deleteFavourite(name: UiPokemon)
}


@Database(entities = [DbPokemon::class, Container::class, UiPokemon::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao() : PokemonDao
}