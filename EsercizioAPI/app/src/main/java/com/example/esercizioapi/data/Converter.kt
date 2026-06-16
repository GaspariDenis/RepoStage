package com.example.esercizioapi.data

import androidx.room.TypeConverter
import com.example.esercizioapi.network.Ability
import com.example.esercizioapi.network.Infos
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun fromList(list: List<Ability>?) : String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(json : String?) : List<Ability>? {
        if(json == null)
            return null
        val type = object : TypeToken<List<Ability>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun fromListI(list: List<Infos>?) : String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toListI(json : String?) : List<Infos>? {
        if(json == null)
            return null
        val type = object : TypeToken<List<Infos>>() {}.type
        return Gson().fromJson(json, type)
    }
}