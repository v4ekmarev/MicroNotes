package com.develop.micronotes.speech.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.develop.micronotes.speech.compose.calendar.CalendarHeader
import com.develop.micronotes.speech.compose.calendar.DayChip
import com.develop.micronotes.speech.compose.calendar.MonthAndWeekCalendarTitle
import com.develop.micronotes.speech.models.TimeLine
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun NotesScreen(modifier: Modifier) {

    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember(currentDate) { currentDate.yearMonth }
    val startMonth = remember(currentDate) { currentMonth.minusMonths(500) }
    val endMonth = remember(currentDate) { currentMonth.plusMonths(500) }
    val daysOfWeek = remember { daysOfWeek() }
    val selectedDate = remember { mutableStateOf<LocalDate?>(currentDate) }

    val lazyColumnState: LazyListState = rememberLazyListState()

    val weekState = rememberWeekCalendarState(
        startDate = startMonth.firstDay,
        endDate = endMonth.lastDay,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = daysOfWeek.first(),
    )

    val monthState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = {

            }) {

            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row {

                val hours = remember(selectedDate.value) {
                    (0..24).map {
                        TimeLine(
                            time = it,
                            taskList = emptyList()
                        )
                    }
                }

                NotesDayTimeline(
                    modifier = Modifier.width(50.dp),
                    timeList = hours,
                    state = lazyColumnState,
                )

                Column {
                    MonthAndWeekCalendarTitle(monthState, weekState)

                    CalendarHeader(daysOfWeek)

                    WeekCalendar(
                        modifier = Modifier
                            .wrapContentHeight(),
                        state = weekState,
                        dayContent = { day ->
                            val isSelectable = day.position == WeekDayPosition.RangeDate
                            val isSelected = selectedDate.value == day.date

                            Column {
                                DayChip(
                                    day.date,
                                    isSelected = isSelected,
                                    isSelectable = isSelectable,
                                ) { clicked ->
                                    if (isSelectable) {
                                        selectedDate.value = clicked
                                    }
                                }

//                                LazyColumn(state = lazyColumnState) {
                                for (i in 0 until 24) {
                                    Column(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .border(1.dp, Color.Black)
                                    ) {

                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SquareGridColumn(
    rows: Int,
    cellSize: androidx.compose.ui.unit.Dp,
    lineColor: Color = Color(0xFF262626),
    subLineColor: Color = Color(0xFF1E1E1E)
) {
    Box(
        modifier = Modifier
            .width(cellSize)
            .height(cellSize * rows)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val rowHeight = height / rows

            // horizontal lines per row
            for (i in 0..rows) {
                val y = rowHeight * i
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }
            // vertical sublines to form squares (quarters)
            val xs = listOf(0.25f, 0.5f, 0.75f)
            xs.forEach { frac ->
                val x = width * frac
                drawLine(
                    color = subLineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
            }
        }
    }
}

@Preview
@Composable
private fun NotesScreenPreview() {
    NotesScreen(modifier = Modifier)
}