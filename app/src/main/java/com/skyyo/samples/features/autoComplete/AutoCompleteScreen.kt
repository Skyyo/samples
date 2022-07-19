package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AutoCompleteScreen(
    viewModel: AutoCompleteViewModel = hiltViewModel()
) {
    val query = viewModel.query.collectAsState()
    val isExpanded = viewModel.isExpanded.collectAsState()
    val suggestions = viewModel.suggestions.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "AndroidView")
        AndroidViewAutocompleteDropdownWithOutsideFiltering(
            modifier = Modifier.fillMaxWidth(),
            suggestions = viewModel.countries,
            selectedValue = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Native exposed dropdown menu")
        AutocompleteDropdownWithFilteringInside(
            modifier = Modifier.fillMaxWidth(),
            countries = viewModel.countries,
        )
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = "Custom exposed dropdown menu")
        AutocompleteDropdownWithOutsideFiltering(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            onSuggestionSelected = viewModel::onCountrySelected,
            onExpandedChange = viewModel::onExpandedChange,
            onValueChange = viewModel::onCountryEntered,
            onClick = viewModel::onExpandedFieldClick,
            suggestions = suggestions.value,
            expanded = isExpanded.value,
            query = query.value,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "AndroidView")
        AndroidViewAutocompleteDropdownWithOutsideFiltering(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            suggestions = viewModel.countries,
            selectedValue = "",
        )
    }
}
