package com.develop.feature.note.presentation.contract

import com.develop.core.model.Category
import com.develop.feature.contacts.domain.model.AppContact
import com.develop.feature.note.presentation.ReminderUiModel

data class NoteState(
    val id: Long? = null,
    val title: String = "",
    val content: String = "",
    val sortOrder: Long? = null,
    val createdAt: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val reminder: ReminderUiModel = ReminderUiModel(),
    val reminderPermissionAcknowledged: Boolean = false,
    val dialog: DialogType? = null,
    val bottomSheet: BottomSheetType? = null,
    val availableContacts: List<AppContact> = emptyList(),
    val selectedContacts: List<AppContact> = emptyList(),
    val isSharingInProgress: Boolean = false,
)

sealed class BottomSheetType {
    data class SelectCategory(val categories: List<Category>) : BottomSheetType()

    data class SelectReminder(val reminderAtMillis: Long?) : BottomSheetType()
    
    data class ShareWithContacts(
        val contacts: List<AppContact>,
        val selectedContactIds: Set<Long>
    ) : BottomSheetType()
}

sealed class DialogType {
    data object ReminderPermission : DialogType()
    data object ReminderCustom : DialogType()
}