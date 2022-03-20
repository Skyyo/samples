package com.skyyo.samples.features.calendar.week

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.skyyo.samples.features.calendar.day.DayItemWeekView
import java.time.LocalDate

@Composable
fun WeekContent(
    week: Week,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        week.days.forEachIndexed { _, day ->
            val isSelected = remember(selectedDate, day) { day.date == selectedDate }
            DayItemWeekView(
                state = day,
                onDateClick = onDateClick,
                isSelected = isSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
