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
    override fun GetID(): String {
        return name
    }
}

@Serializable
@Entity(tableName = "pokemon",)
data class Pokemon (
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
) {
}

@Entity(tableName = "favourite")
data class Favourite (
    @PrimaryKey
    val PokeName: String
)


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
){
}

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