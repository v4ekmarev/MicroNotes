package com.develop.uikit.components.native

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun NativeImagePicker(
    visible: Boolean,
    config: ImagePickerConfig,
    onResult: (ImagePickerResult) -> Unit
) {
    val context = LocalContext.current
    val updatedOnResult by rememberUpdatedState(onResult)

    val launcher = if (config.allowsMultipleSelection) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(config.maxSelectionCount)
        ) { uris ->
            handleUris(uris, context, updatedOnResult)
        }
    } else {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            handleUris(listOfNotNull(uri), context, updatedOnResult)
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}

@Composable
actual fun rememberImagePickerLauncher(
    config: ImagePickerConfig,
    onResult: (ImagePickerResult) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val updatedOnResult by rememberUpdatedState(onResult)

    val launcher = if (config.allowsMultipleSelection) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(config.maxSelectionCount)
        ) { uris ->
            handleUris(uris, context, updatedOnResult)
        }
    } else {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            handleUris(listOfNotNull(uri), context, updatedOnResult)
        }
    }

    return object : ImagePickerLauncher {
        override fun launch() {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}

private fun handleUris(
    uris: List<Uri>,
    context: android.content.Context,
    onResult: (ImagePickerResult) -> Unit
) {
    if (uris.isEmpty()) {
        onResult(ImagePickerResult.Cancelled)
        return
    }

    try {
        val images = uris.mapNotNull { uri ->
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }

        if (images.isNotEmpty()) {
            onResult(ImagePickerResult.Success(images))
        } else {
            onResult(ImagePickerResult.Error("Failed to load images"))
        }
    } catch (e: Exception) {
        onResult(ImagePickerResult.Error(e.message ?: "Unknown error"))
    }
}
