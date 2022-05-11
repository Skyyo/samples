package com.skyyo.samples.features.autofill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    value: String,
    onFill: ((String) -> Unit),
    vararg autofillTypes: AutofillType
) = composed {
    val autofill = LocalAutofill.current
    var hasFocus by remember { mutableStateOf(false) }
    val autofillNode = remember {
        AutofillNode(onFill = onFill, autofillTypes = autofillTypes.toList())
    }

    remember(hasFocus, value) {
        when (hasFocus) {
            true -> {
                if (value.isEmpty()) {
                    autofill?.requestAutofillForNode(autofillNode)
                } else {
                    autofill?.cancelAutofillForNode(autofillNode)
                }
            }
            false -> autofill?.cancelAutofillForNode(autofillNode)
        }
    }

    LocalAutofillTree.current += autofillNode
    onGloballyPositioned { autofillNode.boundingBox = it.boundsInWindow() }
        .onFocusChanged { focusState ->
            hasFocus = focusState.isFocused
        }
}
