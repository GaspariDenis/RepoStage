package com.example.esercizioapi.network

import com.example.esercizioapi.data.RicercaDati
import kotlinx.serialization.Serializable

@Serializable
data class Container (
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
data class Pokemon (
    val name : String,
    val weight : Int = 0,
    val id : Int,
    val sprites : Sprite = Sprite("", ""),
    val abilities : List<Ability> = listOf(),
    val base_experience : Int = -1,
    val species : Specie = Specie("", "")
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
    val slot : Int
)

@Serializable
data class AbilityInfo(
    val name: String,
    val url : String
)

@Serializable
data class Specie (
    val name : String,
    val url : String,
)