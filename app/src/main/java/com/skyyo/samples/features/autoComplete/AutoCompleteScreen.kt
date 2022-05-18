package com.skyyo.samples.features.autoComplete

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

fun provideCountries(): List<String> {
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

@Composable
fun AutoCompleteScreen() {
    val initialList = remember { provideCountries() }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Custom exposed dropdown menu")
        CustomExposedDropdownMenuSample(modifier = Modifier.fillMaxWidth(), initialList = initialList)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "AndroidView")
        AndroidViewTextFieldWithDropDownSample(
            modifier = Modifier.fillMaxWidth(),
            items = provideCountries(),
            selectedValue = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Native exposed dropdown menu")
        NativeExposedDropDownMenuSample(modifier = Modifier.fillMaxWidth(), initialList = initialList)
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = "Custom exposed dropdown menu")
        CustomExposedDropdownMenuSample(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), initialList = initialList)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "AndroidView")
        AndroidViewTextFieldWithDropDownSample(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            items = provideCountries(),
            selectedValue = "",
        )
    }
}
