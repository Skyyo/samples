package com.skyyo.igdbbrowser.application.models.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Input(val value: String = "", val errorId: Int? = null) : Parcelable