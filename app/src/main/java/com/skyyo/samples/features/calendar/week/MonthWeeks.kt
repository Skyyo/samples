package com.skyyo.samples.features.calendar.week

import java.time.YearMonth

data class MonthWeeks(
    val month: YearMonth,
    val weeks: List<Week>
)
