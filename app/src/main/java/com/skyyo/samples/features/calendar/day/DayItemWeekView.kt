package com.skyyo.samples.features.calendar.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun DayItemWeekView(
    state: Day,
    isSelected: Boolean,
    modifier: Modifier,
    onDateClick: (LocalDate) -> Unit
) {
    val date = remember(state.date) { state.date }
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onDateClick(date)
            }
            .then(
                if (isSelected) {
                    Modifier
                        .background(MaterialTheme.colors.primary, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT),
                color = when {
                    isSelected -> Color.White
                    state.isFromCurrentMonth -> Color.Unspecified
                    else -> Color.Gray
                }
            )
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isSelected -> Color.White
                    state.isFromCurrentMonth -> Color.Unspecified
                    else -> Color.Gray
                }
            )
        }
    }
}
