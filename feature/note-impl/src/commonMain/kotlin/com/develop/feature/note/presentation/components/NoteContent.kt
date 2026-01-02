package com.develop.feature.note.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.develop.feature.note.presentation.contract.NoteAction
import com.develop.feature.note.presentation.contract.NoteState

@Composable
fun NoteContent(
    state: NoteState,
    onAction: (NoteAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TextField(
            value = state.title,
            onValueChange = { onAction(NoteAction.TitleChanged(it)) },
            placeholder = { Text("Название") },
            textStyle = MaterialTheme.typography.titleLarge,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        TextField(
            value = state.content,
            onValueChange = { onAction(NoteAction.ContentChanged(it)) },
            placeholder = { Text("Заметка") },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        if (state.reminder.reminderAt != null) {
            ReminderBadge(
                text = "${state.reminder.reminderDateInput} · ${state.reminder.reminderTimeInput}",
                onClear = { onAction(NoteAction.ReminderCleared) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (state.selectedContacts.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                state.selectedContacts.forEach { contact ->
                    ContactBadge(
                        name = contact.displayName ?: contact.username ?: "Контакт",
                        onClear = { onAction(NoteAction.RemoveSelectedContact(contact.userId)) }
                    )
                }
            }
        }

        state.selectedCategory?.let { category ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(6.dp))
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        NoteBottomBar(onAction = onAction)
    }
}

@Composable
fun NoteBottomBar(
    onAction: (NoteAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Изменено: 22:54",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = {
            onAction.invoke(NoteAction.SelectCategory)
        }) {
            Icon(
                imageVector = Icons.Outlined.Label,
                contentDescription = "Выбрать категорию"
            )
        }
    }
}
