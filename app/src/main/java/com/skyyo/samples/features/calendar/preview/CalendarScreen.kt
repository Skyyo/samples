package com.skyyo.samples.features.calendar.preview

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.features.calendar.Calendar
import com.skyyo.samples.features.calendar.rememberCalendarState

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val isWeekViewEnabled by viewModel.isWeekViewEnabled.collectAsState()
    val calendarState = rememberCalendarState(isCompatView = isWeekViewEnabled)

    remember(isWeekViewEnabled) {
        calendarState.isCompatView = isWeekViewEnabled
        null
    }

    ProvideWindowInsets {
        Column(
            Modifier
                .statusBarsPadding()
                .animateContentSize()) {
            Calendar(calendarState = calendarState)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = viewModel::toggleView) {
                Text(text = "Toggle view")
            }
        }
    }
}