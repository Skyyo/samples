package com.skyyo.samples.features.languagePicker

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.skyyo.samples.extensions.FixInAppLanguageSwitchLayoutDirection
import java.util.*

// so we don't waste time on splitting SUPPORTED_LANGUAGES by comma
val SUPPORTED_LANGUAGES_ARRAY: Array<String> = arrayOf("en", "ja", "iw", "uk")

@Composable
fun LanguagePickerScreen() = FixInAppLanguageSwitchLayoutDirection {
    val locales = remember { getSupportedLocales() }
    val currentLocale = remember { getCurrentLocale() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(32.dp)
    ) {
        Text(
            text = "Current language: ${currentLocale?.language ?: "System language"}",
            modifier = Modifier.padding(top = 10.dp)
        )
        repeat(locales.size + 1) { index ->
            val language = if (index == 0) null else locales[index - 1]
            key(index) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language?.language == currentLocale?.language,
                        onClick = { setLocale(language) }
                    )
                    Text(text = language?.language ?: "System language", modifier = Modifier.padding(start = 10.dp))
                }
            }
        }
    }
}

private fun setLocale(locale: Locale?) {
    if (locale == null) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    } else {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
    }
}

private fun getCurrentLocale(): Locale? {
    val currentLanguageTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    return when {
        currentLanguageTag.isEmpty() -> null
        else -> Locale.forLanguageTag(currentLanguageTag)
    }
}

private fun getSupportedLocales(): List<Locale> {
    return SUPPORTED_LANGUAGES_ARRAY.map { Locale.forLanguageTag(it) }
}
