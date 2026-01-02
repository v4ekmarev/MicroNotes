package com.develop.feature.note.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.develop.uikit.core.clickableWithoutRipple
import com.develop.feature.contacts.domain.model.AppContact
import com.develop.feature.note.presentation.ReminderMapper.formatReminderDateInput
import com.develop.feature.note.presentation.ReminderMapper.formatReminderTimeInput
import com.develop.feature.note.presentation.contract.BottomSheetType
import com.develop.feature.note.presentation.contract.NoteAction
import com.develop.feature.note.presentation.contract.NoteState
import com.develop.feature.note.presentation.ReminderQuickOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteBottomSheets(
    state: NoteState,
    onAction: (NoteAction) -> Unit,
) {
    when (val bottomSheet = state.bottomSheet) {
        is BottomSheetType.SelectCategory -> {
            ModalBottomSheet(
                onDismissRequest = { onAction.invoke(NoteAction.DismissBottomSheet) },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Выберите категорию",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    bottomSheet.categories.forEach { category ->
                        val isSelected = category.id == state.selectedCategory?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableWithoutRipple {
                                    onAction.invoke(NoteAction.CategorySelected(category))
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier.width(24.dp)
                                )
                            }

                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Spacer(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        is BottomSheetType.SelectReminder -> {
            ModalBottomSheet(
                onDismissRequest = { onAction.invoke(NoteAction.DismissBottomSheet) },
            ) {
                ReminderSheetContent(state = state, onAction = onAction)
            }
        }
        
        is BottomSheetType.ShareWithContacts -> {
            ModalBottomSheet(
                onDismissRequest = { onAction.invoke(NoteAction.DismissBottomSheet) },
            ) {
                ShareContactsSheetContent(
                    contacts = bottomSheet.contacts,
                    selectedContactIds = bottomSheet.selectedContactIds,
                    onToggleContact = { contactId ->
                        onAction.invoke(NoteAction.ToggleContactSelection(contactId))
                    },
                    onConfirm = { onAction.invoke(NoteAction.ConfirmShareSelection) }
                )
            }
        }

        null -> Unit
    }
}

@Composable
private fun ReminderSheetContent(state: NoteState, onAction: (NoteAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Напомнить позже",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        listOf(
            ReminderQuickOption.TodayEvening,
            ReminderQuickOption.TomorrowMorning,
            ReminderQuickOption.Custom,
        ).forEach { option ->
            ReminderOptionRow(
                title = option.title,
                subtitle = option.subtitle.ifEmpty { 
                    state.reminder.reminderDateInput.takeIf { it.isNotBlank() }
                        ?.let { "$it · ${state.reminder.reminderTimeInput}" }
                },
                selected = state.reminder.selectedReminderOption == option,
                onClick = {
                    onAction.invoke(NoteAction.ReminderQuickOptionSelected(option))
                }
            )
        }
    }
}

@Composable
private fun ReminderOptionRow(
    title: String,
    subtitle: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithoutRipple(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ShareContactsSheetContent(
    contacts: List<AppContact>,
    selectedContactIds: Set<Long>,
    onToggleContact: (Long) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Поделиться с контактами",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (contacts.isEmpty()) {
            Text(
                text = "Нет доступных контактов",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            contacts.forEach { contact ->
                val isSelected = contact.userId in selectedContactIds
                ContactRow(
                    contact = contact,
                    selected = isSelected,
                    onClick = { onToggleContact(contact.userId) }
                )
            }
            
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            
            androidx.compose.material3.Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedContactIds.isEmpty()) 
                        "Готово" 
                    else 
                        "Выбрано: ${selectedContactIds.size}"
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: AppContact,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithoutRipple(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Spacer(modifier = Modifier.width(32.dp))
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.displayName ?: contact.username ?: "Пользователь",
                style = MaterialTheme.typography.bodyLarge,
            )
            contact.phone?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
