package com.skyyo.composespacex.application.models.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ParcelableTestObject(
    val id: Int,
    val name: String,
    val number: Int?
) : Parcelable