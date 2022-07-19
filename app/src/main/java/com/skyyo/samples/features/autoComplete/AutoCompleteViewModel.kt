package com.skyyo.samples.features.autoComplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val QUERY = "query"
private const val SUGGESTIONS = "suggestions"
private const val IS_EXPANDED = "isExpanded"

@HiltViewModel
class AutoCompleteViewModel @Inject constructor(
    private val handle: SavedStateHandle
) : ViewModel() {

    val countries = provideCountries()
    val query = handle.getStateFlow( QUERY, "")
    val suggestions = handle.getStateFlow( SUGGESTIONS, countries)
    val isExpanded = handle.getStateFlow( IS_EXPANDED, false)

    fun onCountryEntered(input: String) {
        handle[QUERY] = input

        viewModelScope.launch(Dispatchers.Default) {
            handle[SUGGESTIONS] = if (input.isEmpty()) {
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
        handle[IS_EXPANDED] = value
    }

    fun onCountrySelected(value: String) {
        handle[QUERY] = value
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
