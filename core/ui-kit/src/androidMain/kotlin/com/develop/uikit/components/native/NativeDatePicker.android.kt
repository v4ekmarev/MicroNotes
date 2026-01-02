package com.develop.uikit.components.native

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun NativeDatePicker(
    state: NativeDatePickerState,
    modifier: Modifier,
    onDateSelected: ((Long) -> Unit)?
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.selectedDateMillis,
        initialDisplayMode = DisplayMode.Picker
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            state.setSelection(millis)
            onDateSelected?.invoke(millis)
        }
    }

    DatePicker(
        state = datePickerState,
        modifier = modifier,
        showModeToggle = false
    )
}
