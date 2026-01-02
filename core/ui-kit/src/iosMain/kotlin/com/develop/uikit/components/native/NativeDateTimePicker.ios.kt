package com.develop.uikit.components.native

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeDateTimePicker(
    state: NativeDateTimePickerState,
    modifier: Modifier,
    onDateTimeSelected: ((Long) -> Unit)?
) {
    val updatedOnDateTimeSelected by rememberUpdatedState(onDateTimeSelected)

    val dateTimePicker = remember {
        IOSDateTimePicker(millis = state.selectedDateTimeMillis) { millis ->
            state.setSelection(millis)
            updatedOnDateTimeSelected?.invoke(millis)
        }
    }

    val size = remember(dateTimePicker) {
        dateTimePicker.sizeThatFits(cValue { CGSizeZero })
            .useContents { DpSize(width.dp, height.dp) }
    }

    val dark = isSystemInDarkTheme()

    UIKitView<UIDatePicker>(
        factory = {
            dateTimePicker.apply { applyTheme(dark) }
        },
        modifier = modifier.size(size),
        update = {
            it.preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
            it.setDate(NSDate.dateWithTimeIntervalSince1970(state.selectedDateTimeMillis / 1000.0), animated = false)
            it.applyTheme(dark)
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
private class IOSDateTimePicker(
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
        datePickerMode = UIDatePickerMode.UIDatePickerModeDateAndTime
        preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
        addTarget(
            target = this,
            action = NSSelectorFromString("dateTimeChanged:"),
            forControlEvents = UIControlEventValueChanged
        )
    }

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    @Suppress("UNUSED")
    fun dateTimeChanged(picker: IOSDateTimePicker) {
        onChange((picker.date.timeIntervalSince1970 * 1000).toLong())
    }
}
