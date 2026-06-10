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
    val weight : Int,
    val id : Int,
    val sprites : Sprite
)

@Serializable
data class Sprite(
    val front_default : String?
)