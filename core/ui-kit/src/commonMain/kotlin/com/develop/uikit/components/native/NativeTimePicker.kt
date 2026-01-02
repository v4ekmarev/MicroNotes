package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * State for NativeTimePicker.
 * @param initialHour Initial hour (0-23).
 * @param initialMinute Initial minute (0-59).
 */
@Stable
class NativeTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0
) {
    var hour: Int by mutableIntStateOf(initialHour.coerceIn(0, 23))
        internal set

    var minute: Int by mutableIntStateOf(initialMinute.coerceIn(0, 59))
        internal set

    fun setTime(hour: Int, minute: Int) {
        this.hour = hour.coerceIn(0, 23)
        this.minute = minute.coerceIn(0, 59)
    }

    companion object {
        val Saver: Saver<NativeTimePickerState, *> = listSaver(
            save = { listOf(it.hour, it.minute) },
            restore = { NativeTimePickerState(it[0], it[1]) }
        )
    }
}

@Composable
fun rememberNativeTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0
): NativeTimePickerState = rememberSaveable(saver = NativeTimePickerState.Saver) {
    NativeTimePickerState(initialHour, initialMinute)
}

/**
 * Native time picker.
 * - iOS: Uses UIDatePicker in time mode (wheel style)
 * - Android: Uses Material3 TimePicker
 *
 * ## Usage example:
 * ```kotlin
 * val timeState = rememberNativeTimePickerState(
 *     initialHour = 10,
 *     initialMinute = 30
 * )
 *
 * NativeTimePicker(
 *     state = timeState,
 *     onTimeSelected = { hour, minute ->
 *         println("Selected time: $hour:$minute")
 *     }
 * )
 *
 * // Access selected time
 * val hour = timeState.hour     // 0-23
 * val minute = timeState.minute // 0-59
 * ```
 */
@Composable
expect fun NativeTimePicker(
    state: NativeTimePickerState,
    modifier: Modifier = Modifier,
    onTimeSelected: ((hour: Int, minute: Int) -> Unit)? = null
)
