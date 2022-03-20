package com.skyyo.samples.features.calendar.month

import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.skyyo.samples.features.calendar.CalendarState
import com.skyyo.samples.features.calendar.day.dec
import com.skyyo.samples.features.calendar.day.getWeeks
import com.skyyo.samples.features.calendar.day.inc
import com.skyyo.samples.features.calendar.week.MonthWeeks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.YearMonth

const val PAGE_COUNT = 3
const val VISIBLE_PAGER_INDEX = 1

@OptIn(ExperimentalPagerApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Stable
class CalendarPagerState(
    coroutineScope: CoroutineScope,
    private val calendarState: CalendarState,
    private val pagerState: PagerState,
    private val onVisibleMonthChanged: (YearMonth) -> Unit
) {

    private var previousMonth by mutableStateOf(
        MonthWeeks(calendarState.visibleMonth.dec(), calendarState.visibleMonth.dec().getWeeks())
    )
    private var currentMonth by mutableStateOf(
        MonthWeeks(calendarState.visibleMonth, calendarState.visibleMonth.getWeeks())
    )
    private var nextMonth by mutableStateOf(
        MonthWeeks(calendarState.visibleMonth.inc(), calendarState.visibleMonth.inc().getWeeks())
    )

    fun getMonthForIndex(index: Int) = when (index) {
        0 -> previousMonth
        1 -> currentMonth
        2 -> nextMonth
        else -> throw IllegalArgumentException()
    }

    fun getWeekForIndex(index: Int) = when (index) {
        0 -> when {
            calendarState.visibleWeek > 0 -> currentMonth.weeks[calendarState.visibleWeek - 1]
            else -> previousMonth.weeks.last()
        }
        1 -> currentMonth.weeks[calendarState.visibleWeek]
        2 -> when {
            calendarState.visibleWeek < currentMonth.weeks.size - 1 -> currentMonth.weeks[calendarState.visibleWeek + 1]
            else -> nextMonth.weeks.first()
        }
        else -> throw IllegalArgumentException()
    }

    init {
        snapshotFlow { calendarState.visibleMonth }.onEach { month ->
            moveToMonth(month)
        }.launchIn(coroutineScope)

        snapshotFlow { if (!pagerState.isScrollInProgress) pagerState.currentPage else VISIBLE_PAGER_INDEX }
            .distinctUntilChanged()
            .onEach { newIndex ->
                if (newIndex == VISIBLE_PAGER_INDEX || pagerState.isScrollInProgress) return@onEach
                if (calendarState.isCompatView) {
                    onScrolled(newIndex)
                } else {
                    val visibleMonth = getMonthForIndex(newIndex)
                    calendarState.visibleMonth = visibleMonth.month
                    onVisibleMonthChanged(visibleMonth.month)
                }
                pagerState.scrollToPage(if (pagerState.pageCount == 0) 0 else VISIBLE_PAGER_INDEX)
            }.launchIn(coroutineScope)
    }

    private fun moveToMonth(month: YearMonth) {
        if (month == currentMonth.month) return
        if (month > currentMonth.month) {
            calendarState.visibleWeek = 0
        } else {
            calendarState.visibleWeek = previousMonth.weeks.size - 1
        }
        previousMonth = MonthWeeks(month.dec(), month.dec().getWeeks())
        currentMonth = MonthWeeks(month, month.getWeeks())
        nextMonth = MonthWeeks(month.inc(), month.inc().getWeeks())
    }

    private fun onScrolled(newIndex: Int) {
        when (newIndex) {
            2 -> {
                if (calendarState.visibleWeek < currentMonth.weeks.size - 1) {
                    calendarState.visibleWeek = calendarState.visibleWeek + 1
                } else {
                    onVisibleMonthChanged(nextMonth.month)
                    calendarState.visibleMonth = nextMonth.month
                    calendarState.visibleWeek = 0
                }
            }
            else -> {
                if (calendarState.visibleWeek != 0) {
                    calendarState.visibleWeek = calendarState.visibleWeek - 1
                } else {
                    onVisibleMonthChanged(previousMonth.month)
                    calendarState.visibleMonth = previousMonth.month
                    calendarState.visibleWeek = previousMonth.weeks.size - 1
                }
            }
        }
    }
}
