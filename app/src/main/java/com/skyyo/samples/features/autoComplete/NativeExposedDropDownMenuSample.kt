package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NativeExposedDropDownMenuSample(modifier: Modifier = Modifier, initialList: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(initialList) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = {
                query = it
                options = if (it.isEmpty()) initialList
                else {
                    val filteredList = initialList.filter {
                        it.lowercase().startsWith(query.lowercase()) && it != query
                    }
                    filteredList
                }
            },
            label = { Text("Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        query = selectionOption
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}
