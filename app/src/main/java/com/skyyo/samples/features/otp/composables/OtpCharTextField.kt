package com.skyyo.samples.features.otp.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpCharTextField(
    value: TextFieldValue,
    cursorBrushColor: Brush,
    textStyle: TextStyle,
    shape: RoundedCornerShape,
    borderStroke: BorderStroke,
    focusRequester: FocusRequester,
    onCharChanged: (String) -> Unit,
    onFocused: (Boolean) -> Unit,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions
) {
    BasicTextField(
        cursorBrush = cursorBrushColor,
        value = value,
        onValueChange = { newTextFieldValue ->
            val newText = newTextFieldValue.text
            if (newText.isDigitsOnly()) onCharChanged(newText)
        },
        modifier = Modifier
            .border(borderStroke, shape)
            .size(36.dp, 48.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { onFocused(it.isFocused) }
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                    onCharChanged("")
                    true
                } else {
                    false
                }
            },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                innerTextField()
            }
        },
    )
}
