package com.develop.uikit.components.native

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun NativeTimePicker(
    state: NativeTimePickerState,
    modifier: Modifier,
    onTimeSelected: ((hour: Int, minute: Int) -> Unit)?
) {
    val timePickerState = rememberTimePickerState(
        initialHour = state.hour,
        initialMinute = state.minute,
        is24Hour = true
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        state.setTime(timePickerState.hour, timePickerState.minute)
        onTimeSelected?.invoke(timePickerState.hour, timePickerState.minute)
    }

    TimePicker(
        state = timePickerState,
        modifier = modifier
    )
}
