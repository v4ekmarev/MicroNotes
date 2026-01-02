package com.develop.micronotes.speech.compose.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.displayText
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CalendarHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.displayText(),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Preview(showBackground = true, name = "Calendar Header")
@Composable
private fun CalendarHeaderPreview() {
    CalendarHeader(
        daysOfWeek = DayOfWeek.entries
    )
}