package com.skyyo.samples.features.languagePicker

sealed class LanguageEvent {
    class ShowConfirmationDialog(val newLanguage: Language): LanguageEvent()
    class UpdateUIWithNewLanguage(val newLanguageCode: String): LanguageEvent()
}
