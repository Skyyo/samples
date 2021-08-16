package com.skyyo.igdbbrowser.features.samples.forceTheme

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.igdbbrowser.application.persistance.DataStoreManager
import com.skyyo.igdbbrowser.features.signIn.THEME_DARK
import com.skyyo.igdbbrowser.features.signIn.THEME_LIGHT
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