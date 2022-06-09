package com.skyyo.samples.features.calendar

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.YearMonth

@Stable
class CalendarState(
    visibleMonth: YearMonth,
    visibleWeek: Int,
    isCompatView: Boolean
) {

    private var _visibleMonth by mutableStateOf(visibleMonth)
    private var _visibleWeek by mutableStateOf(visibleWeek)
    private var _isCompatView by mutableStateOf(isCompatView)

    var visibleMonth: YearMonth
        get() = _visibleMonth
        set(value) { _visibleMonth = value }
    var visibleWeek: Int
        get() = _visibleWeek
        set(value) { _visibleWeek = value }
    var isCompatView: Boolean
        get() = _isCompatView
        set(value) { _isCompatView = value }

    companion object {
        val Saver: Saver<CalendarState, *> = listSaver(
            save = { listOf(it.visibleMonth.toString(), it.visibleWeek.toString(), it._isCompatView.toString()) },
            restore = { CalendarState(YearMonth.parse(it[0]), it[1].toInt(), it[2].toBoolean()) }
        )
    }
}

@Composable
fun rememberCalendarState(
    visibleMonth: YearMonth = YearMonth.now(),
    visibleWeek: Int = 0,
    isCompatView: Boolean = false
): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(visibleMonth, visibleWeek, isCompatView)
    }
}
