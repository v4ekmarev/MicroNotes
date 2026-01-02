package com.develop.micronotes

import com.develop.core.common.Context
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual fun shareText(context: Context, text: String) {
    val message = "Присоединяйся к MicroNotes! $text"
    val activityController = UIActivityViewController(
        activityItems = listOf(message),
        applicationActivities = null
    )
    
    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
        activityController,
        animated = true,
        completion = null
    )
}
