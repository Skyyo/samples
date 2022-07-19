package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AutocompleteDropdownWithFilteringInside(
    modifier: Modifier = Modifier,
    countries: List<String>,
) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val defaultLocale = remember { Locale.getDefault() }
    val lowerCaseSearchQuery = remember(query) { query.lowercase(defaultLocale) }
    var suggestions by remember { mutableStateOf(countries) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardOptions = remember {
        KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
    }
    val keyboardActions = remember {
        KeyboardActions(
            onNext = {
                expanded = false
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    }

    LaunchedEffect(lowerCaseSearchQuery) {
        coroutineScope.launch {
            withContext(Dispatchers.Default) {
                suggestions = when (lowerCaseSearchQuery.isEmpty()) {
                    true -> countries
                    false -> {
                        countries.filter { country ->
                            country.lowercase(defaultLocale).startsWith(lowerCaseSearchQuery) && country != query
                        }
                    }
                }
                expanded = true
            }
        }
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = {
                query = it
            },
            label = { Text("Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { selectionOption ->
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
