package com.skyyo.samples.application.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val id: Int,
    val name: String
) : Parcelable