package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.skyyo.samples.utils.OnValueChange

@Composable
fun OutlinedTextFieldWithValueSwitch(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: OnValueChange = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSelectionChange: (TextRange) -> Unit = {},
) {
    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
    // of the composition.
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    // Last String value that either text field was recomposed with or updated in the onValueChange
    // callback. We keep track of it to prevent calling onValueChange(String) for same String when
    // CoreTextField's onValueChange is called multiple times without recomposition in between.
    var lastTextValue by remember(value) { mutableStateOf(value) }
    val valueSwitched = remember(textFieldValueState, value) { textFieldValueState.text != value }

    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
    // composition.
    val textFieldValue = when {
        valueSwitched -> textFieldValueState.copy(text = value, TextRange(value.length))
        else -> textFieldValueState.copy(text = value)
    }

    OutlinedTextField(
        interactionSource = interactionSource,
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { newTextFieldValue ->
            if (textFieldValueState.selection != newTextFieldValue.selection) {
                onSelectionChange(newTextFieldValue.selection)
            }
            textFieldValueState = newTextFieldValue
            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValue.text
            lastTextValue = newTextFieldValue.text
            if (stringChangedSinceLastInvocation) onValueChange(newTextFieldValue.text)
        },
        label = label,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
fun TextFieldWithValueSwitch(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: OnValueChange = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSelectionChange: (TextRange) -> Unit = {},
) {
    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
    // of the composition.
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    // Last String value that either text field was recomposed with or updated in the onValueChange
    // callback. We keep track of it to prevent calling onValueChange(String) for same String when
    // CoreTextField's onValueChange is called multiple times without recomposition in between.
    var lastTextValue by remember(value) { mutableStateOf(value) }
    val valueSwitched = remember(textFieldValueState, value) { textFieldValueState.text != value }

    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
    // composition.
    val textFieldValue = when {
        valueSwitched -> textFieldValueState.copy(text = value, TextRange(value.length))
        else -> textFieldValueState.copy(text = value)
    }

    TextField(
        interactionSource = interactionSource,
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { newTextFieldValue ->
            if (textFieldValueState.selection != newTextFieldValue.selection) {
                onSelectionChange(newTextFieldValue.selection)
            }
            textFieldValueState = newTextFieldValue
            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValue.text
            lastTextValue = newTextFieldValue.text
            if (stringChangedSinceLastInvocation) onValueChange(newTextFieldValue.text)
        },
        label = label,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}
