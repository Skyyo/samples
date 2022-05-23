package com.skyyo.samples.features.autoComplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.getStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AutoCompleteViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {

    val countries = provideCountries()
    val query = handle.getStateFlow(viewModelScope, "query", "")
    val suggestions = handle.getStateFlow(viewModelScope, "suggestions", countries)
    val isExpanded = handle.getStateFlow(viewModelScope, "isExpanded", false)

    fun onCountryEntered(input: String) {
        query.value = input

        viewModelScope.launch(Dispatchers.Default) {
            suggestions.value = if (input.isEmpty()) {
                countries.also { onExpandedChange(false) }
            } else {
                val filteredList = countries.filter { country ->
                    country
                        .lowercase()
                        .startsWith(input.lowercase()) && country != input
                }
                filteredList.also { onExpandedChange(true) }
            }
        }
    }

    fun onExpandedChange(value: Boolean) {
        isExpanded.value = value
    }

    fun onCountrySelected(value: String) {
        query.value = value
    }

    fun onExpandedFieldClick() {
        onExpandedChange(!isExpanded.value)
    }

    private fun provideCountries(): List<String> {
        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country: String = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        return countries
    }
}
