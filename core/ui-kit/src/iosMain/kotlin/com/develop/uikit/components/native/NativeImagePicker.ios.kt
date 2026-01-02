package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.concurrent.AtomicInt

@Composable
actual fun NativeImagePicker(
    visible: Boolean,
    config: ImagePickerConfig,
    onResult: (ImagePickerResult) -> Unit
) {
    if (!visible) return

    val controller = LocalUIViewController.current
    val updatedOnResult by rememberUpdatedState(onResult)

    val delegate = remember {
        ImagePickerDelegate { result ->
            updatedOnResult(result)
        }
    }

    val pickerController = remember(config) {
        createPickerController(config, delegate)
    }

    DisposableEffect(pickerController) {
        controller.presentViewController(pickerController, animated = true, completion = null)
        onDispose {
            if (controller.presentedViewController == pickerController) {
                controller.dismissViewControllerAnimated(true, null)
            }
        }
    }
}

@Composable
actual fun rememberImagePickerLauncher(
    config: ImagePickerConfig,
    onResult: (ImagePickerResult) -> Unit
): ImagePickerLauncher {
    val controller = LocalUIViewController.current
    val updatedOnResult by rememberUpdatedState(onResult)

    val delegate = remember {
        ImagePickerDelegate { result ->
            updatedOnResult(result)
        }
    }

    return remember(config) {
        object : ImagePickerLauncher {
            override fun launch() {
                val picker = createPickerController(config, delegate)
                controller.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

private fun createPickerController(
    config: ImagePickerConfig,
    delegate: ImagePickerDelegate
): PHPickerViewController {
    val configuration = PHPickerConfiguration().apply {
        filter = PHPickerFilter.imagesFilter
        selectionLimit = if (config.allowsMultipleSelection) config.maxSelectionCount.toLong() else 1L
        selection = PHPickerConfigurationSelectionOrdered
    }
    return PHPickerViewController(configuration).apply {
        this.delegate = delegate
    }
}

private class ImagePickerDelegate(
    private val onResult: (ImagePickerResult) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, null)

        @Suppress("UNCHECKED_CAST")
        val results = didFinishPicking as List<PHPickerResult>

        if (results.isEmpty()) {
            onResult(ImagePickerResult.Cancelled)
            return
        }

        val images = mutableListOf<ImageBitmap>()
        val remaining = AtomicInt(results.size)
        val hasError = AtomicInt(0)

        results.forEach { result ->
            result.itemProvider.loadDataRepresentationForTypeIdentifier(
                typeIdentifier = "public.image"
            ) { data, error ->
                if (error != null || data == null) {
                    hasError.incrementAndGet()
                } else {
                    val image = dataToImageBitmap(data)
                    if (image != null) {
                        images.add(image)
                    }
                }

                if (remaining.decrementAndGet() == 0) {
                    if (images.isNotEmpty()) {
                        onResult(ImagePickerResult.Success(images.toList()))
                    } else if (hasError.value > 0) {
                        onResult(ImagePickerResult.Error("Failed to load images"))
                    } else {
                        onResult(ImagePickerResult.Cancelled)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun dataToImageBitmap(data: NSData): ImageBitmap? {
    return try {
        val bytes = ByteArray(data.length.toInt())
        memcpy(bytes.refTo(0), data.bytes, data.length)
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.toByteArray(): ByteArray? {
    val data = UIImageJPEGRepresentation(this, 0.9) ?: return null
    val bytes = ByteArray(data.length.toInt())
    memcpy(bytes.refTo(0), data.bytes, data.length)
    return bytes
}
