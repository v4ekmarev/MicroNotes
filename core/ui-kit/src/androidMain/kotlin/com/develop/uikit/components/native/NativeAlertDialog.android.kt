package com.develop.uikit.components.native

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun NativeAlertDialog(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    buttons: NativeAlertDialogActionsScope.() -> Unit
) {
    val buttonsList = remember {
        AndroidAlertDialogButtonsScopeImpl().apply(buttons).buttons
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let { { Text(it) } },
        text = message?.let { { Text(it) } },
        confirmButton = {
            buttonsList.filter { it.style != AlertActionStyle.Cancel }.forEach { button ->
                TextButton(
                    onClick = {
                        button.onClick()
                        onDismissRequest()
                    },
                    enabled = button.enabled
                ) {
                    Text(
                        text = button.title,
                        color = if (button.style == AlertActionStyle.Destructive) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        },
        dismissButton = {
            buttonsList.filter { it.style == AlertActionStyle.Cancel }.forEach { button ->
                TextButton(
                    onClick = {
                        button.onClick()
                        onDismissRequest()
                    },
                    enabled = button.enabled
                ) {
                    Text(button.title)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun NativeActionSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    buttons: NativeAlertDialogActionsScope.() -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState()
    val buttonsList = remember {
        AndroidAlertDialogButtonsScopeImpl().apply(buttons).buttons
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            buttonsList.forEach { button ->
                TextButton(
                    onClick = {
                        button.onClick()
                        onDismissRequest()
                    },
                    enabled = button.enabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = button.title,
                        color = when (button.style) {
                            AlertActionStyle.Destructive -> MaterialTheme.colorScheme.error
                            AlertActionStyle.Cancel -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}

private data class AndroidAlertButton(
    val title: String,
    val style: AlertActionStyle,
    val enabled: Boolean,
    val onClick: () -> Unit
)

private class AndroidAlertDialogButtonsScopeImpl : NativeAlertDialogActionsScope {
    val buttons = mutableListOf<AndroidAlertButton>()

    override fun action(
        title: String,
        style: AlertActionStyle,
        enabled: Boolean,
        onClick: () -> Unit
    ) {
        buttons += AndroidAlertButton(title, style, enabled, onClick)
    }
}
