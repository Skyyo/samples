package com.skyyo.samples.application.fragment

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.skyyo.samples.R
import com.skyyo.samples.extensions.observeBackStackStateHandle
import com.skyyo.samples.features.languagePicker.ARG_TEXT
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SecondTabViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher
): ViewModel() {
    private lateinit var firstTabHandle: SavedStateHandle
    val firstTabInput: StateFlow<String>
        get() = firstTabHandle.getStateFlow(FIRST_TAB_INPUT, "")

    init {
        navigationDispatcher.observeBackStackStateHandle(R.id.firstTabFragment) {
            firstTabHandle = it
        }
    }

    fun setFirstTabInput(newInput: String) {
        firstTabHandle[FIRST_TAB_INPUT] = newInput
    }

    fun goLanguage() = navigationDispatcher.emit { it.navigate(R.id.goLanguage, bundleOf(ARG_TEXT to "123")) }
}