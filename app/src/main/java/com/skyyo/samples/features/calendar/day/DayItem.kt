package com.skyyo.samples.features.calendar.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DayItem(
    state: Day,
    isSelected: Boolean,
    modifier: Modifier,
    onDateClick: (LocalDate) -> Unit
) {
    val date = remember(state.date) {
        state.date
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(CircleShape)
            .clickable {
                onDateClick(date)
            }
            .then(
                if (isSelected) {
                    Modifier
                        .shadow(5.dp, CircleShape)
                        .background(MaterialTheme.colors.primary, CircleShape)
                } else {
                    Modifier
                }
            ),
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.align(Alignment.Center),
            color = when {
                isSelected -> Color.White
                state.isFromCurrentMonth -> Color.Unspecified
                else -> Color.Gray
            }
        )
        if (state.hasEvents && !isSelected) {
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .background(MaterialTheme.colors.primary, CircleShape)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
