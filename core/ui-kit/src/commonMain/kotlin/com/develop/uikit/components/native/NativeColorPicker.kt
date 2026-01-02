package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Native color picker.
 * - iOS: Uses UIColorPickerViewController (full-featured native picker)
 * - Android: Custom color picker dialog with preset colors and alpha slider
 *
 * ## Usage example:
 * ```kotlin
 * var showColorPicker by remember { mutableStateOf(false) }
 * var selectedColor by remember { mutableStateOf(Color.Red) }
 *
 * // Color preview
 * Box(
 *     modifier = Modifier
 *         .size(50.dp)
 *         .background(selectedColor)
 *         .clickable { showColorPicker = true }
 * )
 *
 * if (showColorPicker) {
 *     NativeColorPicker(
 *         color = selectedColor,
 *         onColorChanged = { newColor ->
 *             selectedColor = newColor
 *         },
 *         onDismissRequest = { showColorPicker = false },
 *         supportsAlpha = true // enable alpha channel
 *     )
 * }
 * ```
 */
@Composable
expect fun NativeColorPicker(
    color: Color,
    onColorChanged: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    supportsAlpha: Boolean = true
)
