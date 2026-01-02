package com.develop.uikit.components.native

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun NativeShareSheet(
    visible: Boolean,
    content: ShareContent,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val updatedOnDismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(visible, content) {
        if (visible) {
            shareContent(context, content)
            updatedOnDismiss()
        }
    }
}

@Composable
actual fun rememberShareSheetLauncher(
    onDismiss: () -> Unit
): ShareSheetLauncher {
    val context = LocalContext.current
    val updatedOnDismiss by rememberUpdatedState(onDismiss)

    return remember {
        object : ShareSheetLauncher {
            override fun share(content: ShareContent) {
                shareContent(context, content)
                updatedOnDismiss()
            }
        }
    }
}

private fun shareContent(context: Context, content: ShareContent) {
    val intent = when (content) {
        is ShareContent.Text -> createTextShareIntent(content)
        is ShareContent.Url -> createUrlShareIntent(content)
        is ShareContent.Image -> createImageShareIntent(context, content)
        is ShareContent.Multiple -> createMultipleShareIntent(context, content)
    }

    val chooserIntent = Intent.createChooser(intent, null)
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooserIntent)
}

private fun createTextShareIntent(content: ShareContent.Text): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, content.text)
        content.subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
    }
}

private fun createUrlShareIntent(content: ShareContent.Url): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, content.url)
        content.title?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
    }
}

private fun createImageShareIntent(context: Context, content: ShareContent.Image): Intent {
    val uri = saveImageToCache(context, content.imageBytes, content.mimeType)
    return Intent(Intent.ACTION_SEND).apply {
        type = content.mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

private fun createMultipleShareIntent(context: Context, content: ShareContent.Multiple): Intent {
    val uris = ArrayList<Uri>()
    val texts = mutableListOf<String>()

    content.items.forEach { item ->
        when (item) {
            is ShareContent.Text -> texts.add(item.text)
            is ShareContent.Url -> texts.add(item.url)
            is ShareContent.Image -> {
                saveImageToCache(context, item.imageBytes, item.mimeType)?.let { uris.add(it) }
            }
            is ShareContent.Multiple -> {} // Ignore nested multiple
        }
    }

    return if (uris.isNotEmpty()) {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            if (texts.isNotEmpty()) {
                putExtra(Intent.EXTRA_TEXT, texts.joinToString("\n"))
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texts.joinToString("\n"))
        }
    }
}

private fun saveImageToCache(context: Context, imageBytes: ByteArray, mimeType: String): Uri? {
    return try {
        val extension = when {
            mimeType.contains("png") -> "png"
            mimeType.contains("gif") -> "gif"
            mimeType.contains("webp") -> "webp"
            else -> "jpg"
        }
        val file = File(context.cacheDir, "share_image_${System.currentTimeMillis()}.$extension")
        file.writeBytes(imageBytes)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) {
        null
    }
}
