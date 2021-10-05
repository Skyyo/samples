package com.skyyo.samples.application.persistance.room

import androidx.room.TypeConverter
import com.skyyo.samples.application.models.Cat
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
    fun fromCat(data: Cat): String = moshi.adapter(Cat::class.java).toJson(data)

    @TypeConverter
    @JvmStatic
    @ToJson
    fun toCat(json: String): Cat? = moshi.adapter(Cat::class.java).fromJson(json)


}
