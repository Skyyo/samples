package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private const val ITEMS_ABOVE_AND_BELOW_DROPDOWN_ANCHOR = 10

@Composable
fun AutoCompleteScreen(
    viewModel: AutoCompleteViewModel = hiltViewModel()
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val isExpanded by viewModel.isExpanded.collectAsState()
    val query by viewModel.query.collectAsState()
    val isDropdownVisible by viewModel.isDropdownVisible.collectAsState()
    val filteredCountries by viewModel.filteredCountries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AutoCompleteContentColumn(
                title = "AndroidView",
                modifier = Modifier.weight(1f).imePadding()
            ) {
                AndroidViewDropdown(
                    modifier = Modifier.fillMaxWidth(),
                    countries = viewModel.countries,
                    selectedValue = "",
                    filter = viewModel.filter
                )
            }
            AutoCompleteContentColumn(
                title = "Old menu",
                modifier = Modifier.weight(1f).imePadding()
            ) {
                AutocompleteDropdownWithOutsideFiltering(
                    modifier = Modifier.fillMaxWidth(),
                    onSuggestionSelected = viewModel::onCountrySelected,
                    onExpandedChange = viewModel::onExpandedChange,
                    onValueChange = viewModel::onCountryEntered,
                    onClick = viewModel::onExpandedFieldClick,
                    suggestions = suggestions,
                    expanded = isExpanded,
                    query = query,
                )
            }
            AutoCompleteContentColumn(
                title = "New menu",
                modifier = Modifier.weight(1f).imePadding()
            ) {
                AutoCompleteDropdown(
                    modifier = Modifier.fillMaxWidth(),
                    scrollInteractionSource = it.interactionSource,
                    items = filteredCountries,
                    query = query,
                    isDropdownVisible = isDropdownVisible,
                    onQueryChanged = viewModel::onCountryEnteredNew,
                    onItemSelected = viewModel::onCountrySelectedNew
                )
            }
        }
        if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AutoCompleteContentColumn(
    modifier: Modifier,
    title: String,
    dropdownContent: @Composable (ScrollState) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val keyboardActions = remember {
        KeyboardActions { focusManager.moveFocus(FocusDirection.Next) }
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(top = 50.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        repeat(ITEMS_ABOVE_AND_BELOW_DROPDOWN_ANCHOR) {
            TextFieldWithValueSwitch(
                value = "text $it",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = keyboardActions
            )
        }
        dropdownContent(scrollState)
        repeat(ITEMS_ABOVE_AND_BELOW_DROPDOWN_ANCHOR) {
            TextFieldWithValueSwitch(
                value = "text ${it + ITEMS_ABOVE_AND_BELOW_DROPDOWN_ANCHOR}",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = keyboardActions
            )
        }
        TextFieldWithValueSwitch(
            value = "text 21",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = remember {
                KeyboardActions { keyboardController?.hide() }
            }
        )
    }
}
