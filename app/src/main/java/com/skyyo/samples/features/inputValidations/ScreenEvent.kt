package com.skyyo.samples.features.inputValidations

import androidx.compose.ui.focus.FocusDirection

sealed class ScreenEvent {
    class ShowToast(val messageId: Int) : ScreenEvent()
    class UpdateKeyboard(val show: Boolean) : ScreenEvent()
    class RequestFocus(val textFieldKey: FocusedTextFieldKey) : ScreenEvent()
    object ClearFocus : ScreenEvent()
    class MoveFocus(val direction: FocusDirection = FocusDirection.Down) : ScreenEvent()
}
