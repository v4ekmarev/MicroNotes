package com.develop.feature.note.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.develop.feature.note.presentation.contract.NoteAction
import com.develop.feature.note.presentation.contract.NoteState
import com.develop.uikit.components.picker.DateTimePicker
import com.develop.uikit.components.picker.rememberDateTimePickerState
import com.develop.uikit.core.ExperimentalApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ReminderPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Понятно")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        title = { Text("Разрешите уведомления") },
        text = {
            Text("Чтобы напоминать о заметке, нужны разрешения на уведомления. Пока просто сообщаем об этом.")
        }
    )
}

@OptIn(ExperimentalApi::class, ExperimentalTime::class)
@Composable
fun ReminderCustomDialog(
    state: NoteState,
    onAction: (NoteAction) -> Unit,
) {
    val dateTimePickerState = rememberDateTimePickerState(
        initialSelectedDateMillis = state.reminder.reminderAt ?: Clock.System.now().toEpochMilliseconds(),
        initialHour = state.reminder.initialHour,
        initialMinute = state.reminder.initialMinute,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = { onAction.invoke(NoteAction.ReminderCustomDialogDismissed) },
        confirmButton = {
            TextButton(
                onClick = {
                    onAction.invoke(NoteAction.ReminderDateTimeSelected(dateTimePickerState.selectedDateTimeMillis))
                }
            ) {
                Text("Готово")
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction.invoke(NoteAction.ReminderCustomDialogDismissed) }) {
                Text("Отмена")
            }
        },
        title = { Text("Выбрать дату и время") },
        text = {
            DateTimePicker(
                state = dateTimePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}
