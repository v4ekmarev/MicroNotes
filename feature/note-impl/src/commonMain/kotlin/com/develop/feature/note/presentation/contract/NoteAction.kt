package com.develop.feature.note.presentation.contract

import com.develop.core.model.Category
import com.develop.feature.note.presentation.ReminderQuickOption

sealed interface NoteAction {

    data class TitleChanged(val title: String) : NoteAction

    data class ContentChanged(val content: String) : NoteAction

    data object SaveNote : NoteAction

    data object DeleteNote : NoteAction

    data object SelectCategory : NoteAction

    data class CategorySelected(val category: Category) : NoteAction

    data object SelectReminder : NoteAction

    data class ReminderQuickOptionSelected(val option: ReminderQuickOption) : NoteAction

    data class ReminderDateChanged(val value: String) : NoteAction

    data class ReminderTimeChanged(val value: String) : NoteAction

    data class ReminderDateTimeSelected(val millis: Long) : NoteAction

    data object ReminderCustomDialogDismissed : NoteAction

    data object ReminderCustomDialogRequested : NoteAction

    data object ReminderCleared : NoteAction

    data object ReminderPermissionDialogConfirmed : NoteAction

    data object ReminderPermissionDialogDismissed : NoteAction

    data object DismissBottomSheet : NoteAction
    
    data object SelectShareContacts : NoteAction
    
    data class ToggleContactSelection(val contactId: Long) : NoteAction
    
    data object ConfirmShareSelection : NoteAction
    
    data class RemoveSelectedContact(val contactId: Long) : NoteAction
}
