package com.skyyo.samples.features.calendar.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.samples.extensions.getStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val isWeekViewEnabled = savedStateHandle.getStateFlow(viewModelScope, "isWeekViewEnabled", false)

    fun toggleView() {
        isWeekViewEnabled.value = !isWeekViewEnabled.value
    }

}