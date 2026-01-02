package com.develop.uikit.components.native

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun NativeDateTimePicker(
    state: NativeDateTimePickerState,
    modifier: Modifier,
    onDateTimeSelected: ((Long) -> Unit)?
) {
    val calendar = remember { Calendar.getInstance(TimeZone.getTimeZone("UTC")) }
    
    calendar.timeInMillis = state.selectedDateTimeMillis
    val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = calendar.get(Calendar.MINUTE)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.selectedDateTimeMillis,
        initialDisplayMode = DisplayMode.Picker
    )

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val combinedMillis by remember {
        derivedStateOf {
            val dateMillis = datePickerState.selectedDateMillis ?: state.selectedDateTimeMillis
            calendar.timeInMillis = dateMillis
            calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
            calendar.set(Calendar.MINUTE, timePickerState.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
    }

    LaunchedEffect(combinedMillis) {
        state.setSelection(combinedMillis)
        onDateTimeSelected?.invoke(combinedMillis)
    }

    Column(modifier = modifier) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
        TimePicker(
            state = timePickerState
        )
    }
}
