package com.example.esercizioapi.network

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.esercizioapi.data.RicercaDati
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "container")
data class Container (
    @PrimaryKey
    val count : Int,
    val next : String?,
    val previous : String?,
    val results : List<Infos>
)

@Serializable
data class Infos(
    val name : String,
    val url : String,
) : RicercaDati {
    override fun getID(): String {
        return name
    }
}

@Entity(tableName = "pokemon")
data class DbPokemon (
    val name : String,
    val weight : Int = 0,
    @PrimaryKey
    val id : Int,
    @Embedded
    val sprites : Sprite = Sprite("", ""),
    val abilities : List<Ability> = listOf(),
    val base_experience : Int = -1,
    @Embedded
    val species : Specie = Specie("", "")
){
    constructor(item : NetPokemon) : this(
        name = item.name,
        weight = item.weight,
        id = item.id,
        sprites = item.sprites,
        abilities = item.abilities,
        base_experience = item.base_experience,
        species = item.species
    )
}

@Serializable
data class NetPokemon (
    val name : String,
    val weight : Int = 0,
    val id : Int,
    val sprites : Sprite = Sprite("", ""),
    val abilities : List<Ability> = listOf(),
    val base_experience : Int = -1,
    val species : Specie = Specie("", "")
)

//Se si trova dentro la tabella è un preferito
@Entity(tableName = "favourite")
data class UiPokemon (
    val name : String,
    val weight : Int = 0,
    @PrimaryKey
    val id : Int,
    @Embedded
    val sprites : Sprite = Sprite("", ""),
    val abilities : List<Ability> = listOf(),
    val base_experience : Int = -1,
    @Embedded
    val species : Specie = Specie("", "")
){
    constructor(item : DbPokemon) : this(
        name = item.name,
        weight = item.weight,
        id = item.id,
        sprites = item.sprites,
        abilities = item.abilities,
        base_experience = item.base_experience,
        species = item.species
    )
}


@Serializable
data class Sprite(
    val front_default : String?,
    val front_shiny: String?
)

@Serializable
data class Ability(
    val ability : AbilityInfo,
    val is_hidden : Boolean,
    @PrimaryKey
    val slot : Int,
)

@Serializable
data class AbilityInfo(
    val name: String,
    val url : String
)

@Serializable
data class Specie (
    @ColumnInfo(name = "SpecieName") val name : String,
    val url : String,
)