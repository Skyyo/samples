package com.skyyo.igdbbrowser.features.inputValidations.autoDebounce


sealed class UserInputEvent {
    class Name(val input: String) : UserInputEvent()
    class CreditCard(val input: String) : UserInputEvent()
}
