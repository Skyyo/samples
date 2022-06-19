package com.skyyo.samples.application.fragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.observeNavigationResult
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val FIRST_TAB_INPUT = "input"

@HiltViewModel
class FirstTabViewModel @Inject constructor(
    handle: SavedStateHandle,
    navigationDispatcher: NavigationDispatcher
): ViewModel() {
    val input = handle.getStateFlow(FIRST_TAB_INPUT, "")

    init {
        navigationDispatcher.observeNavigationResult(viewModelScope, FIRST_TAB_INPUT, "") {
            handle[FIRST_TAB_INPUT] = it
        }
    }
}