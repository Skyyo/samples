package com.skyyo.samples.features.forceTheme

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.application.persistance.DataStoreManager
import com.skyyo.samples.features.sampleContainer.THEME_DARK
import com.skyyo.samples.features.sampleContainer.THEME_LIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ForceThemeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {


    private val _events = Channel<Boolean>(Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    fun setAppTheme(appTheme: String) {
        val mode = when (appTheme) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        viewModelScope.launch {
            dataStoreManager.setAppTheme(appTheme)
            _events.send(true)
        }
    }
}