package com.skyyo.samples.features.calendar

import CalendarPager
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skyyo.samples.features.calendar.month.MonthHeader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek,
    calendarState: CalendarState = rememberCalendarState(),
    selectedDate: LocalDate = LocalDate.now(),
    onDateClick: (LocalDate) -> Unit = {},
    onVisibleMonthChanged: (YearMonth) -> Unit = {}
) {
    Column(
        modifier = modifier,
    ) {
        MonthHeader(calendarState = calendarState, onVisibleMonthChanged = onVisibleMonthChanged)
        CalendarPager(
            firstDayOfWeek = firstDayOfWeek,
            calendarState = calendarState,
            modifier = modifier,
            onDateClick = onDateClick,
            selectedDate = selectedDate,
            onVisibleMonthChanged = onVisibleMonthChanged
        )
    }
}
