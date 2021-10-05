package com.skyyo.samples.application.persistance.room.cats

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cats_remote_keys")
data class CatsRemoteKeys(
    @PrimaryKey
    val catId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)