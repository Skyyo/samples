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

const val SUPPORTED_LANGUAGES = "en,ja,iw,uk"

//so we don't waste time on splitting SUPPORTED_LANGUAGES by comma
val SUPPORTED_LANGUAGES_ARRAY: Array<String> = arrayOf("en", "ja", "iw", "uk")

@Composable
fun LanguagePickerScreen() = FixInAppLanguageSwitchLayoutDirection {
    val locales = remember { getSupportedLocales() }
    val currentLocale = remember { getCurrentLocale()?.toString() ?: "System default" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(32.dp)
    ) {
        Text(
            text = "Current language: $currentLocale",
            modifier = Modifier.padding(top = 10.dp)
        )
        repeat(locales.size() + 1) { index ->
            val language = if (index == 0) "System default" else "${locales[index - 1]}"
            key(index) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language == currentLocale,
                        onClick = { setLocale(language) }
                    )
                    Text(text = language, modifier = Modifier.padding(start = 10.dp))
                }
            }
        }

    }
}

private fun setLocale(language: String) {
    val languageToSet = if (language == "System default") null else language
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageToSet))
}

private fun getCurrentLocale(): Locale? {
    return AppCompatDelegate.getApplicationLocales().getFirstMatch(SUPPORTED_LANGUAGES_ARRAY)
//    return getSystemService(LocaleManager::class.java).applicationLocales.getFirstMatch(
//        SUPPORTED_LANGUAGES_ARRAY
//    )
}

private fun getSupportedLocales(): LocaleListCompat {
    return LocaleListCompat.forLanguageTags(SUPPORTED_LANGUAGES)
}