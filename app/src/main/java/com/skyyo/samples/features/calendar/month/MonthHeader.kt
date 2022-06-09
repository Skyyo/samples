package com.skyyo.samples.features.calendar.month

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skyyo.samples.features.calendar.CalendarState
import com.skyyo.samples.features.calendar.day.dec
import com.skyyo.samples.features.calendar.day.inc
import java.time.YearMonth

@Composable
fun MonthHeader(
    calendarState: CalendarState,
    modifier: Modifier = Modifier,
    onVisibleMonthChanged: (YearMonth) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                calendarState.visibleMonth = calendarState.visibleMonth.dec()
                onVisibleMonthChanged(calendarState.visibleMonth)
            }
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }
        Text(
            text = calendarState.visibleMonth.month.name.lowercase().replaceFirstChar { it.titlecase() },
            fontWeight = FontWeight.W600
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = calendarState.visibleMonth.year.toString(), fontWeight = FontWeight.W600)
        IconButton(
            onClick = {
                calendarState.visibleMonth = calendarState.visibleMonth.inc()
                onVisibleMonthChanged(calendarState.visibleMonth)
            }
        ) {
            Icon(
                Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }
    }
}
