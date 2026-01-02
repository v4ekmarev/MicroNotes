package com.develop.micronotes.speech.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.develop.micronotes.speech.models.TimeLine
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun NotesDayTimeline(
    modifier: Modifier = Modifier,
    timeList: List<TimeLine>,
    state: LazyListState,
    hourHeight: Dp = 80.dp,
    lineColor: Color = Color.Red,
    backgroundColor: Color = Color(0xFF121212),
) {

    var currentLocalTime by remember { mutableStateOf(currentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            currentLocalTime = currentTime()
        }
    }

    val currentHour = currentLocalTime.hour
    val currentMinute = currentLocalTime.minute
    val currentTimeLabel =
        currentLocalTime.format()//"%02d:%02d".format(currentHour, currentMinute)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp)
    ) {
        LazyColumn(state = state) {
            items(timeList, key = { it.time }) { timeLine ->
                Row(modifier = Modifier.fillMaxSize().height(hourHeight)) {
                    Text(
                        text = timeLine.time.formatHour(),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        // Vertical grid lines inside the hour row (quarters)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridColor = Color(0xFF1E1E1E)
                            val xs = listOf(0.25f, 0.5f, 0.75f)
                            xs.forEach { frac ->
                                val x = size.width * frac
                                drawLine(
                                    color = gridColor,
                                    start = Offset(x, 0f),
                                    end = Offset(x, size.height),
                                    strokeWidth = 1f
                                )
                            }
                        }
                        HorizontalDivider(
                            color = Color(0xFF262626),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                        )

                        if (timeLine.time == currentHour) {
                            val density = LocalDensity.current
                            val yPx = with(density) { hourHeight.toPx() * (currentMinute / 60f) }
                            val yDp = with(density) { yPx.toDp() }
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = lineColor,
                                    start = Offset(0f, yPx),
                                    end = Offset(size.width, yPx),
                                    strokeWidth = 3f
                                )
                                // current time dot at the right edge
                                val dotRadius = 3.dp.toPx()
                                drawCircle(
                                    color = Color.White,
                                    radius = dotRadius,
                                    center = Offset(size.width - dotRadius * 2, yPx)
                                )
                            }

                            Text(
                                text = currentTimeLabel,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .offset(x = (-8).dp, y = yDp)
                                    .background(lineColor, shape = MaterialTheme.shapes.small)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotesDayTimelinePreview() {

    val timeLineList = (0 until 25).map { hour ->
        TimeLine(time = hour, taskList = emptyList())
    }
    val state: LazyListState = rememberLazyListState()

    NotesDayTimeline(
        timeList = timeLineList,
        state = state
    )
}

@OptIn(ExperimentalTime::class)
private fun currentTime(): LocalTime {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return currentTime.time
}

fun LocalTime.format(pattern: String = "HH:mm"): String {
    var result = pattern

    // Replace longer tokens first to avoid overlapping (HH before H)
    result = result.replace("HH", hour.toString().padStart(2, '0'))
    result = result.replace("H", hour.toString())

    result = result.replace("mm", minute.toString().padStart(2, '0'))
    result = result.replace("m", minute.toString())

    result = result.replace("ss", second.toString().padStart(2, '0'))
    result = result.replace("s", second.toString())

    return result
}

fun Int.formatHour(): String {
    return "${this.toString().padStart(2, '0')}:00"
}
