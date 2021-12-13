package com.skyyo.samples.application.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class AssetsList(
    val data: List<Asset>
)

@Parcelize
@JsonClass(generateAdapter = true)
@Entity
data class Asset(
    @PrimaryKey
    val id: String,
    val name: String,
    val priceUsd: String,
    val priceFluctuation: PriceFluctuation = PriceFluctuation.Unknown
) : Parcelable

@Parcelize
enum class PriceFluctuation : Parcelable {
    Unknown,
    Up,
    Down
}