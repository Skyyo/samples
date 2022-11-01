package com.skyyo.samples.features.autoComplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

private const val QUERY = "query"
private const val SUGGESTIONS = "suggestions"
private const val IS_EXPANDED = "isExpanded"
private const val CURRENT_COUNTRY = "country"
private const val LOAD_COUNTRIES_DEBOUNCE = 300L
private const val KEEP_STATE_WHILE_IN_BACKGROUND_TIME = 5000L
private const val FILTER_COUNTRIES_DELAY = 30L

@HiltViewModel
class AutoCompleteViewModel @Inject constructor(
    private val handle: SavedStateHandle,
) : ViewModel() {

    val countries = provideCountries()
    val query = handle.getStateFlow(QUERY, "")
    // DEPRECATED, use filteredCountries instead
    val suggestions = handle.getStateFlow(SUGGESTIONS, countries)
    // DEPRECATED, use rememberSavable in compose instead
    val isExpanded = handle.getStateFlow(IS_EXPANDED, false)

    val isLoading = MutableStateFlow(false)
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val filteredCountries = query
        .map { it }
        .onEach { isLoading.value = true }
        .debounce(LOAD_COUNTRIES_DEBOUNCE)
        .mapLatest { query -> getFilteredCountries(query) }
        .onEach { isLoading.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(KEEP_STATE_WHILE_IN_BACKGROUND_TIME),
            initialValue = emptyList()
        )
    val filter: (String) -> List<String> = { query ->
        countries.filter { it.lowercase().startsWith(query.lowercase()) }
    }
    private val selectedCountry = handle.getStateFlow<String?>(CURRENT_COUNTRY, null)
    val isDropdownVisible = combine(
        isLoading,
        selectedCountry,
        query
    ) { areCitiesLoading, selectedCountry, citiesQuery ->
        when {
            areCitiesLoading -> false
            filteredCountries.value.isEmpty() -> false
            citiesQuery == selectedCountry -> false
            else -> true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(KEEP_STATE_WHILE_IN_BACKGROUND_TIME),
        initialValue = false
    )

    private suspend fun getFilteredCountries(query: String): List<String> {
        delay(FILTER_COUNTRIES_DELAY)
        return filter(query)
    }

    fun onCountryEntered(input: String) {
        handle[QUERY] = input
        viewModelScope.launch(Dispatchers.Default) {
            handle[SUGGESTIONS] = when {
                input.isEmpty() -> countries
                else -> {
                    countries.filter { country ->
                        country
                            .lowercase()
                            .startsWith(input.lowercase()) && country != input
                    }
                }
            }
            onExpandedChange(true)
        }
    }

    fun onCountryEnteredNew(query: String) {
        handle[QUERY] = query
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

    fun onCountrySelectedNew(currentCountry: String) {
        handle[CURRENT_COUNTRY] = currentCountry
        onCountryEnteredNew(currentCountry)
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
