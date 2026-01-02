package com.develop.uikit.components.native

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeZero
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeZoneWithName
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle

private const val MILLIS_IN_MINUTE = 60_000L
private const val MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE
private const val MILLIS_IN_24_HOURS = 24 * MILLIS_IN_HOUR

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeTimePicker(
    state: NativeTimePickerState,
    modifier: Modifier,
    onTimeSelected: ((hour: Int, minute: Int) -> Unit)?
) {
    val updatedOnTimeSelected by rememberUpdatedState(onTimeSelected)

    val millis by remember(state) {
        derivedStateOf {
            (state.hour * MILLIS_IN_HOUR + state.minute * MILLIS_IN_MINUTE)
        }
    }

    val timePicker = remember {
        IOSTimePicker(millis = millis) { newMillis ->
            val totalMinutes = (newMillis % MILLIS_IN_24_HOURS) / MILLIS_IN_MINUTE
            val hour = (totalMinutes / 60).toInt()
            val minute = (totalMinutes % 60).toInt()
            state.setTime(hour, minute)
            updatedOnTimeSelected?.invoke(hour, minute)
        }
    }

    val size = remember(timePicker) {
        timePicker.sizeThatFits(cValue { CGSizeZero })
            .useContents { DpSize(width.dp, height.dp) }
    }

    val dark = isSystemInDarkTheme()

    UIKitView<UIDatePicker>(
        factory = {
            timePicker.apply { applyTheme(dark) }
        },
        modifier = modifier.size(size),
        update = {
            it.preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
            it.setDate(NSDate.dateWithTimeIntervalSince1970(millis / 1000.0), animated = false)
            it.applyTheme(dark)
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
private class IOSTimePicker(
    millis: Long,
    private val onChange: (Long) -> Unit
) : UIDatePicker(CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    init {
        timeZone = NSTimeZone.timeZoneWithName("UTC")
        locale = NSLocale.currentLocale
        setDate(
            date = NSDate.dateWithTimeIntervalSince1970(millis / 1000.0),
            animated = false
        )
        datePickerMode = UIDatePickerMode.UIDatePickerModeTime
        preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
        addTarget(
            target = this,
            action = NSSelectorFromString("timeChanged:"),
            forControlEvents = UIControlEventValueChanged
        )
    }

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    @Suppress("UNUSED")
    fun timeChanged(picker: IOSTimePicker) {
        onChange((picker.date.timeIntervalSince1970 * 1000).toLong())
    }
}
