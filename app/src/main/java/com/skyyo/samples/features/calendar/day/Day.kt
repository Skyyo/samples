package com.skyyo.samples.features.calendar.day

import java.time.LocalDate

data class Day(
    val date: LocalDate,
    val isFromCurrentMonth: Boolean,
    val hasEvents: Boolean
)
