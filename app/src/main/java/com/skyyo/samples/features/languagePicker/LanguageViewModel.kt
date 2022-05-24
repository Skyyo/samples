package com.skyyo.samples.features.languagePicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.persistance.DataStoreManager
import com.skyyo.samples.utils.DEFAULT_LANGUAGE_CODE
import com.skyyo.samples.utils.supportedLanguages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    val events = Channel<LanguageEvent>(Channel.UNLIMITED)
    val currentLanguage = dataStoreManager.getLanguageCode().map { currentLanguageCode ->
        supportedLanguages.first { currentLanguageCode == it.code }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = supportedLanguages.first { it.code == DEFAULT_LANGUAGE_CODE }
    )

    fun changeLanguage(language: Language) {
        if (language != currentLanguage.value) {
            events.trySend(LanguageEvent.ShowConfirmationDialog(language))
        }
    }

    fun applyNewLanguage(language: Language) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreManager.setLanguageCode(language.code)
        events.send(LanguageEvent.UpdateUIWithNewLanguage(language.code))
    }
}