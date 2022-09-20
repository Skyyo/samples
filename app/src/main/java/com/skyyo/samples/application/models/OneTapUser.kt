package com.skyyo.samples.application.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class OneTapUser(val id: String, val name: String, val surname: String, val phone: String = "") : Parcelable {
    companion object {
        val empty = OneTapUser(id = "", name = "", surname = "")
    }
}
