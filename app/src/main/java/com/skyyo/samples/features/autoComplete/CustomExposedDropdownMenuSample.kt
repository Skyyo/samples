package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.skyyo.samples.utils.OnClick
import com.skyyo.samples.utils.OnValueChange

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
// DEPRECATED, use AutoCompleteDropdown instead
fun AutocompleteDropdownWithOutsideFiltering(
    modifier: Modifier = Modifier,
    onExpandedChange: (Boolean) -> Unit,
    onSuggestionSelected: (String) -> Unit,
    onClick: OnClick,
    onValueChange: OnValueChange,
    suggestions: List<String>,
    expanded: Boolean,
    query: String,
) {

    val textFieldValue = remember {
        mutableStateOf(TextFieldValue(query, TextRange(query.length)))
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardOptions = remember {
        KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
    }
    val keyboardActions = remember {
        KeyboardActions(
            onNext = {
                onExpandedChange(false)
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    }

    CustomExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onClick = onClick,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue.value,
            onValueChange = { value ->
                textFieldValue.value = value
                if (value.text != query) onValueChange.invoke(value.text)
            },
            label = { Text("Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )

        if (suggestions.isNotEmpty() && expanded) {
            CustomExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange.invoke(false) }
            ) {
                items(suggestions) { option ->
                    key(option) {
                        DropdownMenuItem(
                            onClick = {
                                textFieldValue.value = TextFieldValue(
                                    text = option,
                                    selection = TextRange(option.length)
                                )
                                onSuggestionSelected.invoke(option)
                                onExpandedChange.invoke(false)
                            }
                        ) {
                            Text(text = option)
                        }
                    }
                }
            }
        }
    }
}
