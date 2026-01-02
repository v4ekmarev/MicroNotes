package com.develop.uikit.components.native

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyle
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIAlertControllerStyleAlert

@Composable
@NonRestartableComposable
actual fun NativeAlertDialog(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    buttons: NativeAlertDialogActionsScope.() -> Unit
) = IOSAlertController(
    onDismissRequest = onDismissRequest,
    title = title,
    message = message,
    style = UIAlertControllerStyleAlert,
    buttons = buttons,
)

@Composable
actual fun NativeActionSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    buttons: NativeAlertDialogActionsScope.() -> Unit
) {
    if (visible) {
        IOSAlertController(
            onDismissRequest = onDismissRequest,
            title = title,
            message = message,
            style = UIAlertControllerStyleActionSheet,
            buttons = buttons,
        )
    }
}

private var isAlertControllerBeingDismissed = false

@Composable
private fun IOSAlertController(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    style: UIAlertActionStyle,
    buttons: NativeAlertDialogActionsScope.() -> Unit,
) {
    val buttonsList = remember {
        IOSAlertDialogButtonsScopeImpl(onDismissRequest = {
            if (!isAlertControllerBeingDismissed) {
                isAlertControllerBeingDismissed = true
                onDismissRequest()
            }
        }).apply(buttons).buttons
    }

    val titles = buttonsList.fastMap { it.title }
    val styles = buttonsList.fastMap { it.style }

    key(titles, styles) {
        PresentationController(
            factory = {
                UIAlertController.alertControllerWithTitle(
                    title = title,
                    message = message,
                    preferredStyle = style
                ).apply {
                    buttonsList.fastForEach { addAction(it) }
                }
            },
            update = {
                setTitle(title)
                setMessage(message)

                val uiActions = actions.filterIsInstance<UIAlertAction>()
                if (uiActions.size == buttonsList.size) {
                    uiActions.zip(buttonsList).fastForEach { (action, button) ->
                        action.setEnabled(button.enabled)
                    }
                }
            },
            onDismissRequest = onDismissRequest,
            title, message
        )
    }
}

private class IOSAlertDialogButtonsScopeImpl(
    val onDismissRequest: () -> Unit,
) : NativeAlertDialogActionsScope {

    val buttons = mutableListOf<UIAlertAction>()

    override fun action(
        title: String,
        style: AlertActionStyle,
        enabled: Boolean,
        onClick: () -> Unit
    ) {
        buttons += UIAlertAction.actionWithTitle(
            title = title,
            style = style.toUIKit(),
            handler = {
                onClick()
                onDismissRequest()
            }
        ).apply {
            setEnabled(enabled)
        }
    }
}

private fun AlertActionStyle.toUIKit(): UIAlertActionStyle = when (this) {
    AlertActionStyle.Default -> UIAlertActionStyleDefault
    AlertActionStyle.Cancel -> UIAlertActionStyleCancel
    AlertActionStyle.Destructive -> UIAlertActionStyleDestructive
}
