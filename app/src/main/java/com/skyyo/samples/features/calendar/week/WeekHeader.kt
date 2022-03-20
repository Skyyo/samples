package com.skyyo.samples.features.calendar.week

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT),
                modifier = modifier
                    .weight(1f)
                    .wrapContentHeight(),
                color = if (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY) Color.Gray else Color.Unspecified
            )
        }
    }
}

fun <T> Array<T>.rotateRight(n: Int): List<T> = takeLast(n) + dropLast(n)
