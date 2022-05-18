package com.skyyo.samples.features.autofill

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    value: String,
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit),
    interactionSource: MutableInteractionSource
) = composed {
    val autofill = LocalAutofill.current
    val localAutofillTree = LocalAutofillTree.current
    var viewBounds by remember { mutableStateOf(Rect.Zero) }
    var hasFocus by remember { mutableStateOf(false) }
    var onFillTime by remember { mutableStateOf(0L) }

    val autofillNode by remember(onFillTime) {
        mutableStateOf(
            AutofillNode(
                onFill = {
                    onFillTime = System.currentTimeMillis()
                    onFill.invoke(it)
                },
                autofillTypes = autofillTypes
            ).apply {
                boundingBox = viewBounds
                localAutofillTree.plusAssign(this)
            }
        )
    }

    remember(hasFocus, value) {
        if (hasFocus && value.isEmpty()) {
            autofill?.requestAutofillForNode(autofillNode)
        } else {
            autofill?.cancelAutofillForNode(autofillNode)
        }
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Release) {
                autofill?.requestAutofillForNode(autofillNode)
            }
        }
    }
    onGloballyPositioned {
        viewBounds = it.boundsInRoot()
        autofillNode.boundingBox = viewBounds
        localAutofillTree.plusAssign(autofillNode)
    }
        .onFocusChanged { focusState ->
            hasFocus = focusState.isFocused
        }
}
