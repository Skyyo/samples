package com.skyyo.igdbbrowser.features.inputValidations

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InputWrapper(val value: String = "", val errorId: Int? = null) : Parcelable