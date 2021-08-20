package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.TypeConverter
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.squareup.moshi.*

object MoshiTypeConverters {
    private val moshi = Moshi.Builder().build()

    private val listOfIntAdapter: JsonAdapter<List<Int>> =
        moshi.adapter<List<Int>>(Types.newParameterizedType(List::class.java, Integer::class.java)).nonNull()

    @TypeConverter
    @JvmStatic
    @FromJson
    fun fromList(value: String): List<Int> = listOfIntAdapter.fromJson(value) as List<Int>

    @TypeConverter
    @JvmStatic
    @ToJson
    fun toList(value: List<Int>?): String? = listOfIntAdapter.toJson(value)

    @TypeConverter
    @JvmStatic
    @FromJson
    fun fromGame(data: Game): String = moshi.adapter(Game::class.java).toJson(data)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun toGame(json: String): Game? = moshi.adapter(Game::class.java).fromJson(json)


}
