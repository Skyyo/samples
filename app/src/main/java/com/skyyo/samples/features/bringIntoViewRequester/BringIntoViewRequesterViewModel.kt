package com.skyyo.samples.features.bringIntoViewRequester

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.getStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BringIntoViewRequesterViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {

    val name = handle.getStateFlow(viewModelScope, "name", "")

    fun onNameEntered(input: String) {
        name.value = input
    }
}
