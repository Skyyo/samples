package com.skyyo.samples.features.calendar.week

import com.skyyo.samples.features.calendar.day.Day

data class Week(
    val isFirstWeekOfTheMonth: Boolean = false,
    val days: List<Day>,
)
