package com.develop.uikit.components.native

import androidx.compose.runtime.Composable

/**
 * Style for alert dialog actions.
 */
enum class AlertActionStyle {
    Default,
    Cancel,
    Destructive
}

/**
 * Scope for building native alert dialog actions.
 */
interface NativeAlertDialogActionsScope {
    fun action(
        title: String,
        style: AlertActionStyle = AlertActionStyle.Default,
        enabled: Boolean = true,
        onClick: () -> Unit
    )
}

fun NativeAlertDialogActionsScope.defaultAction(
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) = action(title, AlertActionStyle.Default, enabled, onClick)

fun NativeAlertDialogActionsScope.cancelAction(
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) = action(title, AlertActionStyle.Cancel, enabled, onClick)

fun NativeAlertDialogActionsScope.destructiveAction(
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) = action(title, AlertActionStyle.Destructive, enabled, onClick)

/**
 * Native alert dialog.
 * - iOS: Uses UIAlertController with alert style
 * - Android: Uses Material3 AlertDialog
 *
 * ## Usage example:
 * ```kotlin
 * var showDialog by remember { mutableStateOf(false) }
 *
 * if (showDialog) {
 *     NativeAlertDialog(
 *         onDismissRequest = { showDialog = false },
 *         title = "Delete Note?",
 *         message = "This action cannot be undone."
 *     ) {
 *         destructiveAction("Delete") {
 *             // perform delete
 *         }
 *         cancelAction("Cancel") {
 *             // cancelled
 *         }
 *     }
 * }
 *
 * // With default action
 * NativeAlertDialog(
 *     onDismissRequest = { showDialog = false },
 *     title = "Success",
 *     message = "Note saved successfully."
 * ) {
 *     defaultAction("OK") { showDialog = false }
 * }
 * ```
 */
@Composable
expect fun NativeAlertDialog(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String? = null,
    buttons: NativeAlertDialogActionsScope.() -> Unit
)

/**
 * Native action sheet (bottom sheet with actions).
 * - iOS: Uses UIAlertController with actionSheet style
 * - Android: Uses Material3 ModalBottomSheet
 *
 * ## Usage example:
 * ```kotlin
 * var showActionSheet by remember { mutableStateOf(false) }
 *
 * NativeActionSheet(
 *     visible = showActionSheet,
 *     onDismissRequest = { showActionSheet = false },
 *     title = "Choose Action",
 *     message = "What would you like to do?"
 * ) {
 *     defaultAction("Edit") { /* edit */ }
 *     defaultAction("Share") { /* share */ }
 *     destructiveAction("Delete") { /* delete */ }
 *     cancelAction("Cancel") { /* cancel */ }
 * }
 * ```
 */
@Composable
expect fun NativeActionSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String? = null,
    message: String? = null,
    buttons: NativeAlertDialogActionsScope.() -> Unit
)
