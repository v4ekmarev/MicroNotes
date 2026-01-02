/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.develop.uikit.components.ComponentText
import com.develop.uikit.components.LocalContainerColor
import com.develop.uikit.components.calendar.PlatformDateFormat
import com.develop.uikit.components.calendar.currentLocale
import com.develop.uikit.components.calendar.defaultLocale
import com.develop.uikit.core.ExperimentalApi
import com.develop.uikit.core.InternalApi
import com.develop.uikit.core.theme.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@ExperimentalApi
fun rememberTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24Hour: Boolean = PlatformDateFormat.is24HourFormat(defaultLocale()),
): TimePickerState = rememberSaveable(
    saver = TimePickerState.Saver()
) {
    TimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
}

@OptIn(InternalApi::class)
@Composable
@ExperimentalApi
fun TimePicker(
    state: TimePickerState,
    height: Dp = PickerDefaults.Height,
    indicator: PickerIndicator = PickerDefaults.indicator(),
    containerColor: Color = LocalContainerColor.current.takeOrElse {
        Theme.colorScheme.secondarySystemGroupedBackground
    },
    modifier: Modifier = Modifier
) {
    LaunchedEffect(state) {
        state.isManual = false
    }

    if (state.is24Hour)
        TimePicker24(
            state = state,
            height = height,
            indicator = indicator,
            containerColor = containerColor,
            modifier = modifier
        )
    else TimePicker12(
        state = state,
        height = height,
        indicator = indicator,
        containerColor = containerColor,
        modifier = modifier
    )
}

@OptIn(ExperimentalApi::class)
@Composable
private fun TimePicker24(
    state: TimePickerState,
    height: Dp = PickerDefaults.Height,
    indicator: PickerIndicator = PickerDefaults.indicator(),
    containerColor: Color = Theme.colorScheme.secondarySystemGroupedBackground,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(height)
            .background(containerColor)
            .pickerIndicator(
                state = state.hourState,
                indicator = indicator
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.widthIn(max = PickerMaxWidth / 2),
            horizontalArrangement = Arrangement.Center,
        ) {
            WheelPicker(
                state = state.hourState,
                items = Hours24,
                height = height,
                modifier = Modifier.weight(1f),
                indicator = {},
                containerColor = containerColor,
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = TimePickerTokens.BlockWidth / 4),
                ) {
                    NumberPickerText(
                        text = it,
                        textAlign = TextAlign.End,
                    )
                }
            }

            WheelPicker(
                state = state.minuteState,
                items = Minutes,
                height = height,
                modifier = Modifier.weight(1f),
                indicator = {},
                containerColor = containerColor,
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = TimePickerTokens.BlockWidth / 4),
                ) {
                    NumberPickerText(
                        text = it,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalApi::class)
@Composable
private fun TimePicker12(
    state: TimePickerState,
    height: Dp = PickerDefaults.Height,
    indicator: PickerIndicator = PickerDefaults.indicator(),
    containerColor: Color = Theme.colorScheme.secondarySystemGroupedBackground,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(height)
            .background(containerColor)
            .pickerIndicator(
                state = state.hourState,
                indicator = indicator
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.widthIn(max = PickerMaxWidth / 2),
            horizontalArrangement = Arrangement.Center,
        ) {
            WheelPicker(
                state = state.hourState,
                items = Hours12,
                height = height,
                modifier = Modifier.weight(1f),
                indicator = {},
                containerColor = containerColor,
            ) {
                Box(modifier = Modifier) {
                    NumberPickerText(
                        text = it,
                        textAlign = TextAlign.End,
                    )
                }
            }

            WheelPicker(
                state = state.minuteState,
                items = Minutes,
                height = height,
                modifier = Modifier.width(TimePickerTokens.BlockWidth),
                indicator = {},
                containerColor = containerColor,
                rotationTransformOrigin = TransformOrigin.Center
            ) {
                NumberPickerText(
                    text = it,
                    textAlign = TextAlign.Center,
                )
            }

            WheelPicker(
                state = state.amPmState,
                items = listOf(true, false),
                height = height,
                modifier = Modifier.weight(1f),
                indicator = {},
                containerColor = containerColor
            ) {
                PickerText(
                    text = if (it) AmPm.first else AmPm.second,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

@Composable
internal fun PickerText(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    textAlign: TextAlign = TextAlign.Start
) {
    ComponentText(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        maxLines = 1,
        minLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun NumberPickerText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    ComponentText(
        text = text,
        modifier = modifier.fillMaxWidth(),
        textAlign = textAlign,
        maxLines = 1,
        minLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontFeatureSettings = "tnum"
    )
}

@OptIn(InternalApi::class)
@Stable
@ExperimentalApi
class TimePickerState internal constructor(
    initialHour: Int,
    initialMinute: Int,
    val is24Hour: Boolean = PlatformDateFormat.is24HourFormat(currentLocale()),
) {
    init {
        require(initialHour in 0..23) { "initialHour should in [0..23] range" }
        require(initialMinute in 0..59) { "initialMinute should be in [0..59] range" }
    }

    val minute: Int by derivedStateOf {
        if (isManual) {
            manualMinute
        } else {
            minuteState.selectedItemIndex.modSign(Minutes.size)
        }
    }

    val hour: Int by derivedStateOf {
        if (isManual) {
            manualHour
        } else {
            if (!is24Hour && isEvening)
                12 + hourState.selectedItemIndex.modSign(hoursList.size)
            else hourState.selectedItemIndex.modSign(hoursList.size)
        }
    }

    private val hoursList: List<String>
        get() = if (is24Hour) Hours24 else Hours12

    internal val isEvening
        get() = amPmState.selectedItemIndex == 1

    internal val hourState = PickerState(
        infinite = true,
        initiallySelectedItemIndex = if (is24Hour) initialHour else initialHour % 12
    )

    internal val minuteState = PickerState(
        infinite = true,
        initiallySelectedItemIndex = initialMinute
    )

    internal val amPmState = PickerState()

    @InternalApi
    var manualHour: Int by mutableStateOf(initialHour)

    @InternalApi
    var manualMinute: Int by mutableStateOf(initialMinute)

    private var _isManual by mutableStateOf(false)

    @InternalApi
    var isManual
        get() = _isManual
        set(value) {
            if (_isManual != value) {
                if (value) {
                    manualHour = hourState.currentSelectedItem(hoursList.size)
                    manualMinute = minuteState.currentSelectedItem(60)
                } else {
                    CoroutineScope(Dispatchers.Unconfined).launch {
                        hourState.scrollToItem(manualHour)
                        minuteState.scrollToItem(manualMinute)
                    }
                }
                _isManual = value
            }
        }

    companion object {
        fun Saver(): Saver<TimePickerState, *> = Saver(
            save = {
                listOf(
                    it.hour,
                    it.minute,
                    it.is24Hour
                )
            },
            restore = { value ->
                TimePickerState(
                    initialHour = value[0] as Int,
                    initialMinute = value[1] as Int,
                    is24Hour = value[2] as Boolean
                )
            }
        )
    }
}

internal object TimePickerTokens {
    val BlockWidth = 85.dp
    val Padding = 24.dp
}

internal fun Int.toStringWithLeadingZero() = if (this < 10) "0$this" else "$this"

internal val Minutes = (0..59).map(Int::toStringWithLeadingZero)
internal val Hours24 = (0..23).map(Int::toStringWithLeadingZero)
internal val Hours12 = (0..11).map(Int::toStringWithLeadingZero)

internal val AmPm = "AM" to "PM"
