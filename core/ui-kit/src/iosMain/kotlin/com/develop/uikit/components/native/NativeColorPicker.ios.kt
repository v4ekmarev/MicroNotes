package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import platform.UIKit.UIColorPickerViewController
import platform.UIKit.UIColorPickerViewControllerDelegateProtocol
import platform.darwin.NSObject

@Composable
actual fun NativeColorPicker(
    color: Color,
    onColorChanged: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    supportsAlpha: Boolean
) {
    val updatedOnColorChanged by rememberUpdatedState(onColorChanged)
    val updatedOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val delegate = remember {
        ColorPickerDelegate(
            onColorChanged = { updatedOnColorChanged(it) },
            onDismissRequest = { updatedOnDismissRequest() }
        )
    }

    PresentationController(
        factory = {
            UIColorPickerViewController().apply {
                this.delegate = delegate
                this.supportsAlpha = supportsAlpha
            }
        },
        update = {
            selectedColor = color.toUIColor()
            this.supportsAlpha = supportsAlpha
        },
        onDismissRequest = updatedOnDismissRequest,
        color, supportsAlpha
    )
}

private class ColorPickerDelegate(
    private val onColorChanged: (Color) -> Unit,
    private val onDismissRequest: () -> Unit
) : NSObject(), UIColorPickerViewControllerDelegateProtocol {
    
    override fun colorPickerViewControllerDidSelectColor(
        viewController: UIColorPickerViewController
    ) {
        onColorChanged(viewController.selectedColor.toComposeColor())
    }

    override fun colorPickerViewControllerDidFinish(
        viewController: UIColorPickerViewController
    ) {
        onDismissRequest()
    }
}
