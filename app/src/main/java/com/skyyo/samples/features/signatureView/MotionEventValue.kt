package com.skyyo.samples.features.signatureView

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MotionEventValue(val eventType: Int, val x: Float, val y: Float) : Parcelable
