package com.skyyo.samples.features.calendar.month

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.skyyo.samples.features.calendar.day.DayItem
import com.skyyo.samples.features.calendar.week.MonthWeeks
import java.time.LocalDate

const val MAX_WEEKS = 6

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MonthContent(
    currentMonth: MonthWeeks,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.wrapContentWidth()) {
        currentMonth.weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.days.forEach { day ->
                    val isSelected = remember(selectedDate, day) { day.date == selectedDate }

                    DayItem(
                        state = day,
                        onDateClick = onDateClick,
                        isSelected = isSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        // imitate empty row for case when nex month have 6 weeks and bottom content will dragging
        if (currentMonth.weeks.size != MAX_WEEKS) {
            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}
