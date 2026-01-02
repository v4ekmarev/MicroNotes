package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Result of image picking operation.
 * 
 * @see ImagePickerResult.Success - contains list of selected images
 * @see ImagePickerResult.Cancelled - user cancelled the picker
 * @see ImagePickerResult.Error - error occurred with message
 */
sealed interface ImagePickerResult {
    data class Success(val images: List<ImageBitmap>) : ImagePickerResult
    data object Cancelled : ImagePickerResult
    data class Error(val message: String) : ImagePickerResult
}

/**
 * Configuration for image picker.
 */
data class ImagePickerConfig(
    val allowsMultipleSelection: Boolean = false,
    val maxSelectionCount: Int = 1,
    val allowsEditing: Boolean = false
)

/**
 * Native image picker.
 * - iOS: Uses PHPickerViewController
 * - Android: Uses ActivityResultContracts.PickVisualMedia
 *
 * @param visible Whether the picker is visible
 * @param config Configuration for the picker
 * @param onResult Callback with the result of picking
 *
 * ## Usage example:
 * ```kotlin
 * var showPicker by remember { mutableStateOf(false) }
 * var selectedImages by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }
 *
 * Button(onClick = { showPicker = true }) {
 *     Text("Select Images")
 * }
 *
 * NativeImagePicker(
 *     visible = showPicker,
 *     config = ImagePickerConfig(
 *         allowsMultipleSelection = true,
 *         maxSelectionCount = 5
 *     ),
 *     onResult = { result ->
 *         when (result) {
 *             is ImagePickerResult.Success -> selectedImages = result.images
 *             is ImagePickerResult.Cancelled -> { /* user cancelled */ }
 *             is ImagePickerResult.Error -> { /* handle error: result.message */ }
 *         }
 *         showPicker = false
 *     }
 * )
 * ```
 */
@Composable
expect fun NativeImagePicker(
    visible: Boolean,
    config: ImagePickerConfig = ImagePickerConfig(),
    onResult: (ImagePickerResult) -> Unit
)

/**
 * Launcher for image picker that can be triggered programmatically.
 */
interface ImagePickerLauncher {
    fun launch()
}

/**
 * Remember an image picker launcher for programmatic triggering.
 *
 * ## Usage example:
 * ```kotlin
 * val imageLauncher = rememberImagePickerLauncher(
 *     config = ImagePickerConfig(allowsMultipleSelection = false),
 *     onResult = { result ->
 *         if (result is ImagePickerResult.Success) {
 *             val image = result.images.first()
 *             // use image
 *         }
 *     }
 * )
 *
 * Button(onClick = { imageLauncher.launch() }) {
 *     Text("Pick Photo")
 * }
 * ```
 */
@Composable
expect fun rememberImagePickerLauncher(
    config: ImagePickerConfig = ImagePickerConfig(),
    onResult: (ImagePickerResult) -> Unit
): ImagePickerLauncher
