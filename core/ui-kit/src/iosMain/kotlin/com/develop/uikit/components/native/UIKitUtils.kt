package com.develop.uikit.components.native

import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreGraphics.CGFloatVar
import platform.UIKit.UIColor
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIView
import platform.UIKit.UIViewController

internal fun UIViewController.applyTheme(dark: Boolean) {
    overrideUserInterfaceStyle = if (dark)
        UIUserInterfaceStyle.UIUserInterfaceStyleDark
    else UIUserInterfaceStyle.UIUserInterfaceStyleLight
}

internal fun UIView.applyTheme(dark: Boolean) {
    listOf(this, superview).forEach {
        it?.overrideUserInterfaceStyle = if (dark)
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        else UIUserInterfaceStyle.UIUserInterfaceStyleLight
    }
}

internal fun Color.toUIColor(): UIColor {
    return UIColor(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble()
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIColor.toComposeColor(): Color = memScoped {
    val red = alloc<CGFloatVar>()
    val green = alloc<CGFloatVar>()
    val blue = alloc<CGFloatVar>()
    val alpha = alloc<CGFloatVar>()
    
    getRed(
        red = red.ptr,
        green = green.ptr,
        blue = blue.ptr,
        alpha = alpha.ptr
    )
    
    Color(
        red = red.value.toFloat(),
        green = green.value.toFloat(),
        blue = blue.value.toFloat(),
        alpha = alpha.value.toFloat()
    )
}
