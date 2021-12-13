package com.skyyo.samples.application.persistance.room.assets

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AssetsRemoteKeys(
    @PrimaryKey
    val assetKey: Int,
    val prevKey: Int?,
    val nextKey: Int?
)