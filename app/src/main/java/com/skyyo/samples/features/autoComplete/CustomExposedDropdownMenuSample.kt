package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomExposedDropdownMenuSample(modifier: Modifier = Modifier, initialList: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var options by remember { mutableStateOf(initialList) }

    CustomExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = { textFieldValue ->
                if (textFieldValue.text == query.text) {
                    query = textFieldValue
                } else {
                    query = textFieldValue
                    options = if (textFieldValue.text.isEmpty()) {
                        initialList.also { expanded = false }
                    } else {
                        val filteredList = initialList.filter { country ->
                            country
                                .lowercase()
                                .startsWith(query.text.lowercase()) && country != query.text
                        }
                        filteredList.also { expanded = true }
                    }
                }
            },
            label = { Text("Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )

        if (options.isNotEmpty()) {
            CustomExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items(options) { option ->
                    key(option) {
                        DropdownMenuItem(
                            onClick = {
                                query = TextFieldValue(
                                    text = option,
                                    selection = TextRange(option.length)
                                )
                                expanded = false
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
