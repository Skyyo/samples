package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.TypeConverter
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.squareup.moshi.*

object MoshiTypeConverters {
    val moshi = Moshi.Builder().build()

    private val listOfIntAdapter: JsonAdapter<List<Int>> =
        moshi.adapter<List<Int>>(Types.newParameterizedType(List::class.java, Integer::class.java))
            .nonNull()

    @TypeConverter
    @JvmStatic
    @ToJson
    fun fromList(value: String): List<Int>? {
        return listOfIntAdapter.fromJson(value) as List<Int>
    }

    @TypeConverter
    @JvmStatic
    @FromJson
    fun toList(value: List<Int>?): String? = listOfIntAdapter.toJson(value)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun fromGame(data: Game): String = moshi.adapter(Game::class.java).toJson(data)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun toGame(json: String): Game? = moshi.adapter(Game::class.java).fromJson(json)


    @TypeConverter
    @JvmStatic
    @ExperimentalStdlibApi
    inline fun <reified T> toJson(clazz: T): String {
        return moshi.adapter<T>().toJson(clazz)
    }


    @TypeConverter
    @JvmStatic
    @ExperimentalStdlibApi
    inline fun <reified T> fromJson(json: String): T? {
        return moshi.adapter<T>().fromJson(json)
    }

}
