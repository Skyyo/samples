package com.skyyo.samples.features.autofill

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutoFilledTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    onFill: ((String) -> Unit),
    autofillTypes: List<AutofillType>,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val autofill = LocalAutofill.current
    val localAutofillTree = LocalAutofillTree.current
    val interactionSource = remember { MutableInteractionSource() }
    var viewBounds by remember { mutableStateOf(Rect.Zero) }
    var hasFocus by remember { mutableStateOf(false) }
    var onFillTime by remember { mutableStateOf(0L) }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    val autofillNode by remember(onFillTime) {
        mutableStateOf(
            AutofillNode(
                onFill = {
                    onFillTime = System.currentTimeMillis()
                    onFill.invoke(it)
                    textFieldValue = TextFieldValue(text = it, selection = TextRange(it.length))
                },
                autofillTypes = autofillTypes
            ).apply {
                boundingBox = viewBounds
                localAutofillTree.plusAssign(this)
            }
        )
    }

    SideEffect {
        when {
            hasFocus && value.isEmpty() -> autofill?.requestAutofillForNode(autofillNode)
            else -> autofill?.cancelAutofillForNode(autofillNode)
        }
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Release) {
                autofill?.requestAutofillForNode(autofillNode)
            }
        }
    }

    OutlinedTextField(
        modifier = modifier
            .onGloballyPositioned {
                viewBounds = it.boundsInRoot()
                autofillNode.boundingBox = viewBounds
                localAutofillTree.plusAssign(autofillNode)
            }
            .onFocusChanged { focusState ->
                hasFocus = focusState.isFocused
            },
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onValueChange(it.text)
        },
        label = label,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
    )
}
