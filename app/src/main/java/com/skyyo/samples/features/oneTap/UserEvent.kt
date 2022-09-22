package com.skyyo.samples.features.oneTap

sealed class UserEvent {
    class ShowToast(val message: String) : UserEvent()
}
