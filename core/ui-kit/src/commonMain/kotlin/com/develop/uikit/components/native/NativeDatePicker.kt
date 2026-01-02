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
 * State for NativeDatePicker.
 * @param initialSelectedDateMillis Initial selected date in milliseconds since epoch (UTC).
 */
@Stable
class NativeDatePickerState(
    initialSelectedDateMillis: Long = currentTimeMillis()
) {
    var selectedDateMillis: Long by mutableLongStateOf(initialSelectedDateMillis)
        internal set

    fun setSelection(millis: Long) {
        selectedDateMillis = millis
    }

    companion object {
        val Saver: Saver<NativeDatePickerState, Long> = Saver(
            save = { it.selectedDateMillis },
            restore = { NativeDatePickerState(it) }
        )
    }
}

@Composable
fun rememberNativeDatePickerState(
    initialSelectedDateMillis: Long = currentTimeMillis()
): NativeDatePickerState = rememberSaveable(saver = NativeDatePickerState.Saver) {
    NativeDatePickerState(initialSelectedDateMillis)
}

/**
 * Native date picker.
 * - iOS: Uses UIDatePicker (wheel style)
 * - Android: Uses Material3 DatePicker
 *
 * ## Usage example:
 * ```kotlin
 * val dateState = rememberNativeDatePickerState(
 *     initialSelectedDateMillis = System.currentTimeMillis()
 * )
 *
 * NativeDatePicker(
 *     state = dateState,
 *     onDateSelected = { millis ->
 *         // Handle date selection
 *         println("Selected: $millis")
 *     }
 * )
 *
 * // Access selected date
 * val selectedDate = dateState.selectedDateMillis
 * ```
 */
@Composable
expect fun NativeDatePicker(
    state: NativeDatePickerState,
    modifier: Modifier = Modifier,
    onDateSelected: ((Long) -> Unit)? = null
)

// Expect System.currentTimeMillis() for multiplatform
internal expect fun currentTimeMillis(): Long
