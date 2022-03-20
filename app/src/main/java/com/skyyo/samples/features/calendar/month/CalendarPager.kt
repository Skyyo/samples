
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skyyo.samples.features.calendar.CalendarState
import com.skyyo.samples.features.calendar.day.DAYS_IN_A_WEEK
import com.skyyo.samples.features.calendar.month.CalendarPagerState
import com.skyyo.samples.features.calendar.month.MonthContent
import com.skyyo.samples.features.calendar.month.PAGE_COUNT
import com.skyyo.samples.features.calendar.month.VISIBLE_PAGER_INDEX
import com.skyyo.samples.features.calendar.week.WeekContent
import com.skyyo.samples.features.calendar.week.WeekHeader
import com.skyyo.samples.features.calendar.week.rotateRight
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalPagerApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CalendarPager(
    calendarState: CalendarState,
    firstDayOfWeek: DayOfWeek,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onVisibleMonthChanged: (YearMonth) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = VISIBLE_PAGER_INDEX, pageCount = PAGE_COUNT)
    val coroutineScope = rememberCoroutineScope()
    val daysOfWeek = remember(firstDayOfWeek) {
        DayOfWeek.values().rotateRight(DAYS_IN_A_WEEK - firstDayOfWeek.ordinal)
    }
    val calendarPagerState = remember {
        CalendarPagerState(
            coroutineScope = coroutineScope,
            calendarState = calendarState,
            pagerState = pagerState,
            onVisibleMonthChanged = onVisibleMonthChanged,
        )
    }
    if (!calendarState.isCompatView) WeekHeader(daysOfWeek, modifier)
    CompositionLocalProvider(LocalOverScrollConfiguration provides null) {
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = modifier
        ) { pageIndex ->
            if (calendarState.isCompatView) {
                WeekContent(
                    week = calendarPagerState.getWeekForIndex(pageIndex),
                    onDateClick = onDateClick,
                    selectedDate = selectedDate
                )
            } else {
                MonthContent(
                    currentMonth = calendarPagerState.getMonthForIndex(pageIndex),
                    selectedDate = selectedDate,
                    onDateClick = onDateClick
                )
            }
        }
    }
}
