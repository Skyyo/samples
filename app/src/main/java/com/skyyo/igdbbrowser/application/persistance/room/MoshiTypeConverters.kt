package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.TypeConverter
import com.skyyo.igdbbrowser.application.models.remote.Launch
import com.skyyo.igdbbrowser.application.models.remote.Rocket
import com.squareup.moshi.*

object MoshiTypeConverters {
    private val moshi = Moshi.Builder().build()

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
    fun fromLaunch(data: Launch): String = moshi.adapter(Launch::class.java).toJson(data)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun toLaunch(json: String): Launch? = moshi.adapter(Launch::class.java).fromJson(json)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun fromRocket(data: Rocket): String = moshi.adapter(Rocket::class.java).toJson(data)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun toRocket(json: String): Rocket? = moshi.adapter(Rocket::class.java).fromJson(json)


}
