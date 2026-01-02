package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * State for NativeDateTimePicker.
 * @param initialSelectedDateTimeMillis Initial selected date and time in milliseconds since epoch (UTC).
 */
@Stable
class NativeDateTimePickerState(
    initialSelectedDateTimeMillis: Long = currentTimeMillis()
) {
    var selectedDateTimeMillis: Long by mutableLongStateOf(initialSelectedDateTimeMillis)
        internal set

    fun setSelection(millis: Long) {
        selectedDateTimeMillis = millis
    }

    companion object {
        val Saver: Saver<NativeDateTimePickerState, Long> = Saver(
            save = { it.selectedDateTimeMillis },
            restore = { NativeDateTimePickerState(it) }
        )
    }
}

@Composable
fun rememberNativeDateTimePickerState(
    initialSelectedDateTimeMillis: Long = currentTimeMillis()
): NativeDateTimePickerState = rememberSaveable(saver = NativeDateTimePickerState.Saver) {
    NativeDateTimePickerState(initialSelectedDateTimeMillis)
}

/**
 * Native date and time picker.
 * - iOS: Uses UIDatePicker in dateAndTime mode (wheel style)
 * - Android: Uses Material3 DatePicker + TimePicker combination
 *
 * ## Usage example:
 * ```kotlin
 * val dateTimeState = rememberNativeDateTimePickerState(
 *     initialSelectedDateTimeMillis = System.currentTimeMillis()
 * )
 *
 * NativeDateTimePicker(
 *     state = dateTimeState,
 *     onDateTimeSelected = { millis ->
 *         println("Selected datetime: $millis")
 *     }
 * )
 *
 * // Access selected datetime
 * val selectedDateTime = dateTimeState.selectedDateTimeMillis
 * ```
 */
@Composable
expect fun NativeDateTimePicker(
    state: NativeDateTimePickerState,
    modifier: Modifier = Modifier,
    onDateTimeSelected: ((Long) -> Unit)? = null
)
