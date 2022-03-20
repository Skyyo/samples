package com.skyyo.samples.features.calendar.day

import com.skyyo.samples.features.calendar.week.Week
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

const val DAYS_IN_A_WEEK = 7

internal infix fun DayOfWeek.daysUntil(other: DayOfWeek) = (DAYS_IN_A_WEEK + (value - other.value)) % DAYS_IN_A_WEEK

fun YearMonth.getWeeks(
    includeAdjacentMonths: Boolean = true
): List<Week> {
    val daysLength = lengthOfMonth()
    val firstDayOfTheWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val starOffset = atDay(1).dayOfWeek daysUntil firstDayOfTheWeek
    val endOffset =
        DAYS_IN_A_WEEK - (atDay(daysLength).dayOfWeek daysUntil firstDayOfTheWeek) - 1

    return (1 - starOffset..daysLength + endOffset).chunked(DAYS_IN_A_WEEK).mapIndexed { index, days ->
        Week(
            isFirstWeekOfTheMonth = index == 0,
            days = days.mapNotNull { dayOfMonth ->
                val (date, isFromCurrentMonth) = when (dayOfMonth) {
                    in Int.MIN_VALUE..0 -> if (includeAdjacentMonths) {
                        val previousMonth = this.dec()
                        previousMonth.atDay(previousMonth.lengthOfMonth() + dayOfMonth) to false
                    } else {
                        return@mapNotNull null
                    }
                    in 1..daysLength -> atDay(dayOfMonth) to true
                    else -> if (includeAdjacentMonths) {
                        val nextMonth = this.inc()
                        nextMonth.atDay(dayOfMonth - daysLength) to false
                    } else {
                        return@mapNotNull null
                    }
                }
                Day(
                    date = date,
                    isFromCurrentMonth = isFromCurrentMonth,
                    false // TODO check date with list of event dates
                )
            }
        )
    }
}

operator fun YearMonth.dec(): YearMonth = this.minusMonths(1)

operator fun YearMonth.inc(): YearMonth = this.plusMonths(1)
