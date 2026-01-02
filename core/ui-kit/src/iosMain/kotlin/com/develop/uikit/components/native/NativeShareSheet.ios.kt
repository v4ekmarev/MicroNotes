package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIImage

@Composable
actual fun NativeShareSheet(
    visible: Boolean,
    content: ShareContent,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val controller = LocalUIViewController.current
    val updatedOnDismiss by rememberUpdatedState(onDismiss)

    val activityItems = remember(content) { content.toActivityItems() }

    val activityController = remember(activityItems) {
        UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        ).apply {
            completionWithItemsHandler = { _, _, _, _ ->
                updatedOnDismiss()
            }
        }
    }

    DisposableEffect(activityController) {
        controller.presentViewController(activityController, animated = true, completion = null)
        onDispose {
            if (controller.presentedViewController == activityController) {
                controller.dismissViewControllerAnimated(true, null)
            }
        }
    }
}

@Composable
actual fun rememberShareSheetLauncher(
    onDismiss: () -> Unit
): ShareSheetLauncher {
    val controller = LocalUIViewController.current
    val updatedOnDismiss by rememberUpdatedState(onDismiss)

    return remember {
        object : ShareSheetLauncher {
            override fun share(content: ShareContent) {
                val activityItems = content.toActivityItems()
                val activityController = UIActivityViewController(
                    activityItems = activityItems,
                    applicationActivities = null
                ).apply {
                    completionWithItemsHandler = { _, _, _, _ ->
                        updatedOnDismiss()
                    }
                }
                controller.presentViewController(activityController, animated = true, completion = null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ShareContent.toActivityItems(): List<Any> = when (this) {
    is ShareContent.Text -> listOfNotNull(text, subject)
    is ShareContent.Url -> listOfNotNull(
        NSURL.URLWithString(url),
        title
    )
    is ShareContent.Image -> {
        val nsData = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        listOfNotNull(UIImage.imageWithData(nsData))
    }
    is ShareContent.Multiple -> items.flatMap { it.toActivityItems() }
}
