package com.skyyo.samples.features.oneTap

sealed class OneTapEvent {
    class ShowToast(val message: String) : OneTapEvent()
}
