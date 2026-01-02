package com.develop.uikit.components.native

import androidx.compose.runtime.Composable

/**
 * Content to share.
 *
 * @see ShareContent.Text - share plain text with optional subject
 * @see ShareContent.Url - share URL with optional title
 * @see ShareContent.Image - share image as byte array
 * @see ShareContent.Multiple - share multiple items at once
 */
sealed interface ShareContent {
    data class Text(val text: String, val subject: String? = null) : ShareContent
    data class Url(val url: String, val title: String? = null) : ShareContent
    data class Image(val imageBytes: ByteArray, val mimeType: String = "image/png") : ShareContent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Image) return false
            return imageBytes.contentEquals(other.imageBytes) && mimeType == other.mimeType
        }
        override fun hashCode(): Int = imageBytes.contentHashCode() * 31 + mimeType.hashCode()
    }
    data class Multiple(val items: List<ShareContent>) : ShareContent
}

/**
 * Native share sheet.
 * - iOS: Uses UIActivityViewController
 * - Android: Uses Intent.ACTION_SEND with chooser
 *
 * @param visible Whether the share sheet is visible
 * @param content Content to share
 * @param onDismiss Callback when share sheet is dismissed
 *
 * ## Usage example:
 * ```kotlin
 * var showShare by remember { mutableStateOf(false) }
 *
 * Button(onClick = { showShare = true }) {
 *     Text("Share")
 * }
 *
 * // Share text
 * NativeShareSheet(
 *     visible = showShare,
 *     content = ShareContent.Text(
 *         text = "Check out this app!",
 *         subject = "App Recommendation"
 *     ),
 *     onDismiss = { showShare = false }
 * )
 *
 * // Share URL
 * NativeShareSheet(
 *     visible = showShare,
 *     content = ShareContent.Url(
 *         url = "https://example.com",
 *         title = "Example Website"
 *     ),
 *     onDismiss = { showShare = false }
 * )
 *
 * // Share image (imageBytes: ByteArray)
 * NativeShareSheet(
 *     visible = showShare,
 *     content = ShareContent.Image(
 *         imageBytes = imageBytes,
 *         mimeType = "image/png"
 *     ),
 *     onDismiss = { showShare = false }
 * )
 * ```
 */
@Composable
expect fun NativeShareSheet(
    visible: Boolean,
    content: ShareContent,
    onDismiss: () -> Unit
)

/**
 * Launcher for share sheet that can be triggered programmatically.
 */
interface ShareSheetLauncher {
    fun share(content: ShareContent)
}

/**
 * Remember a share sheet launcher for programmatic triggering.
 *
 * ## Usage example:
 * ```kotlin
 * val shareLauncher = rememberShareSheetLauncher(
 *     onDismiss = { /* optional: handle dismiss */ }
 * )
 *
 * Button(onClick = {
 *     shareLauncher.share(ShareContent.Text("Hello World!"))
 * }) {
 *     Text("Share")
 * }
 *
 * // Share multiple items
 * Button(onClick = {
 *     shareLauncher.share(
 *         ShareContent.Multiple(
 *             listOf(
 *                 ShareContent.Text("Check this out!"),
 *                 ShareContent.Url("https://example.com")
 *             )
 *         )
 *     )
 * }) {
 *     Text("Share Multiple")
 * }
 * ```
 */
@Composable
expect fun rememberShareSheetLauncher(
    onDismiss: () -> Unit = {}
): ShareSheetLauncher
