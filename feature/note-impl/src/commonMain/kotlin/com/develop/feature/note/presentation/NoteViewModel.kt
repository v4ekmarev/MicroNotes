package com.develop.feature.note.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.develop.feature.note.route.NoteRoute
import com.develop.feature.note.presentation.contract.NoteAction
import com.develop.feature.note.domain.usecase.NoteUseCases
import com.develop.core.notification.NotificationScheduler
import com.develop.core.model.Category
import com.develop.core.model.Note
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import com.develop.feature.note.domain.usecase.ShareNoteParams
import com.develop.feature.note.presentation.contract.BottomSheetType
import com.develop.feature.note.presentation.contract.BottomSheetType.SelectCategory
import com.develop.feature.note.presentation.contract.BottomSheetType.SelectReminder
import com.develop.feature.note.presentation.contract.BottomSheetType.ShareWithContacts
import com.develop.feature.note.presentation.contract.DialogType
import com.develop.feature.note.presentation.contract.NoteEffect
import com.develop.feature.note.presentation.contract.NoteState
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlin.time.Clock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class NoteViewModel(
    private val useCases: NoteUseCases,
    private val notificationScheduler: NotificationScheduler,
    val permissionsController: PermissionsController,
    private val route: NoteRoute,
) : ViewModel() {
    private val _state = MutableStateFlow(NoteState())
    val state: StateFlow<NoteState> = _state

    private val _effect = Channel<NoteEffect>(capacity = Channel.BUFFERED)
    val effect: Flow<NoteEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val details = useCases.getNoteDetails.execute(route.id)
            val note = details.note

            _state.update { current ->
                current.copy(
                    id = note?.id,
                    title = note?.title.orEmpty(),
                    content = note?.content.orEmpty(),
                    sortOrder = note?.sortOrder,
                    createdAt = note?.createdAt,
                    categories = details.categories,
                    selectedCategory = details.categories.firstOrNull { it.id == note?.categoryId }
                        ?: details.categories.firstOrNull(),
                    reminder = ReminderMapper.toUi(note?.reminderAt)
                )
            }
        }
    }

    fun doOnAction(action: NoteAction) {
        when (action) {
            is NoteAction.TitleChanged -> onTitleChanged(action.title)
            is NoteAction.ContentChanged -> onContentChanged(action.content)
            is NoteAction.SaveNote -> onSaveNote()
            is NoteAction.DeleteNote -> onDeleteNote()
            is NoteAction.SelectCategory -> onSelectCategory()
            is NoteAction.CategorySelected -> onCategorySelected(action.category)
            is NoteAction.DismissBottomSheet -> onDismissBottomSheet()
            is NoteAction.ReminderDateTimeSelected -> onReminderDateTimeSelected(action.millis)
            is NoteAction.ReminderCustomDialogRequested -> onReminderCustomDialogRequested()
            is NoteAction.ReminderCustomDialogDismissed -> onReminderCustomDialogDismissed()
            is NoteAction.ReminderDateChanged -> onReminderDateChanged(action.value)
            is NoteAction.ReminderPermissionDialogConfirmed -> onReminderPermissionConfirmed()
            is NoteAction.ReminderPermissionDialogDismissed -> onReminderPermissionDismissed()
            is NoteAction.ReminderQuickOptionSelected -> onReminderQuickOptionSelected(action.option)
            is NoteAction.ReminderTimeChanged -> onReminderTimeChanged(action.value)
            is NoteAction.ReminderCleared -> onReminderCleared()
            is NoteAction.SelectReminder -> onSelectReminder()
            is NoteAction.SelectShareContacts -> onSelectShareContacts()
            is NoteAction.ToggleContactSelection -> onToggleContactSelection(action.contactId)
            is NoteAction.ConfirmShareSelection -> onConfirmShareSelection()
            is NoteAction.RemoveSelectedContact -> onRemoveSelectedContact(action.contactId)
        }
    }

    private fun onTitleChanged(title: String) {
        _state.update { it.copy(title = title) }
    }

    private fun onContentChanged(content: String) {
        _state.update { it.copy(content = content) }
    }

    @OptIn(ExperimentalTime::class)
    private fun onSaveNote() {
        val current = state.value
        val now = Clock.System.now().toEpochMilliseconds()
        viewModelScope.launch {
            val savedNote = Note(
                id = current.id,
                title = current.title,
                content = current.content,
                categoryId = current.selectedCategory?.id,
                reminderAt = current.reminder.reminderAt,
                sortOrder = current.sortOrder ?: now,
                createdAt = current.createdAt ?: now,
                updatedAt = now,
            )
            useCases.saveNote.execute(savedNote)

            // Планируем уведомление, если установлено напоминание
            val noteId = current.id ?: savedNote.createdAt
            val reminderAt = current.reminder.reminderAt
            if (reminderAt != null && reminderAt > now) {
                notificationScheduler.schedule(
                    id = noteId,
                    title = current.title.ifBlank { "Напоминание" },
                    body = current.content.take(100),
                    triggerAtMillis = reminderAt
                )
            } else if (reminderAt == null) {
                // Отменяем уведомление, если напоминание убрано
                notificationScheduler.cancel(noteId)
            }

            // Отправляем заметку выбранным контактам
            if (current.selectedContacts.isNotEmpty()) {
                useCases.shareNote.execute(
                    ShareNoteParams(
                        recipientIds = current.selectedContacts.map { it.userId },
                        title = current.title,
                        content = current.content
                    )
                )
            }

            _effect.trySend(NoteEffect.Back)
        }
    }

    private fun onDeleteNote() {
        // TODO hook up delete when id available
    }

    private fun onSelectCategory() {
        _state.update { it.copy(bottomSheet = SelectCategory(categories = it.categories)) }
    }

    private fun onCategorySelected(category: Category) {
        _state.update { current ->
            current.copy(
                selectedCategory = category,
                bottomSheet = null,
            )
        }
    }

    private fun onDismissBottomSheet() {
        _state.update { it.copy(bottomSheet = null) }
    }

    private fun onSelectReminder() {
        val current = _state.value
        if (!current.reminderPermissionAcknowledged) {
            _state.update { it.copy(dialog = DialogType.ReminderPermission) }
            return
        }
        _state.update {
            it.copy(bottomSheet = SelectReminder(reminderAtMillis = it.reminder.reminderAt))
        }
    }

    private fun onReminderPermissionConfirmed() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.Companion.REMOTE_NOTIFICATION)
                _state.update {
                    it.copy(
                        reminderPermissionAcknowledged = true,
                        bottomSheet = SelectReminder(reminderAtMillis = it.reminder.reminderAt),
                        dialog = null,
                    )
                }
            } catch (e: DeniedAlwaysException) {
                // Разрешение отклонено навсегда — можно показать диалог с переходом в настройки
                _state.update { it.copy(dialog = null) }
            } catch (e: DeniedException) {
                // Разрешение отклонено
                _state.update { it.copy(dialog = null) }
            }
        }
    }

    private fun onReminderPermissionDismissed() {
        _state.update { it.copy(dialog = null) }
    }

    private fun onReminderCustomDialogRequested() {
        _state.update {
            it.copy(
                dialog = DialogType.ReminderCustom,
                reminder = it.reminder.copy(reminderInputError = null)
            )
        }
    }

    private fun onReminderCustomDialogDismissed() {
        _state.update {
            it.copy(
                dialog = null,
                reminder = it.reminder.copy(reminderInputError = null)
            )
        }
    }

    private fun onReminderCleared() {
        _state.update {
            it.copy(reminder = ReminderUiModel())
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun onReminderQuickOptionSelected(option: ReminderQuickOption) {
        when (option) {
            is ReminderQuickOption.Custom -> {
                onReminderCustomDialogRequested()
            }
            else -> {
                val reminderAt = ReminderMapper.calculateReminderTimestamp(option)
                _state.update {
                    it.copy(
                        reminder = it.reminder.copy(
                            reminderAt = reminderAt,
                            initialHour = ReminderMapper.extractHour(reminderAt),
                            initialMinute = ReminderMapper.extractMinute(reminderAt),
                            selectedReminderOption = option,
                            reminderDateInput = ReminderMapper.formatReminderDateInput(reminderAt),
                            reminderTimeInput = ReminderMapper.formatReminderTimeInput(reminderAt),
                            reminderInputError = null,
                        ),
                        bottomSheet = null,
                    )
                }
            }
        }
    }

    private fun onReminderDateChanged(value: String) {
        _state.update {
            it.copy(
                reminder = it.reminder.copy(
                    reminderDateInput = value,
                    selectedReminderOption = null,
                    reminderInputError = null,
                )
            )
        }
    }

    private fun onReminderTimeChanged(value: String) {
        _state.update {
            it.copy(
                reminder = it.reminder.copy(
                    reminderTimeInput = value,
                    selectedReminderOption = null,
                    reminderInputError = null,
                )
            )
        }
    }

    /**
     * Обрабатывает выбор даты/времени из DateTimePicker.
     * DateTimePicker возвращает UTC millis, конвертируем в локальный timestamp.
     */
    private fun onReminderDateTimeSelected(pickerMillis: Long) {
        val localMillis = ReminderMapper.convertPickerResultToLocalTimestamp(pickerMillis)
        _state.update {
            it.copy(
                reminder = it.reminder.copy(
                    reminderAt = localMillis,
                    initialHour = ReminderMapper.extractHour(localMillis),
                    initialMinute = ReminderMapper.extractMinute(localMillis),
                    reminderDateInput = ReminderMapper.formatReminderDateInput(localMillis),
                    reminderTimeInput = ReminderMapper.formatReminderTimeInput(localMillis),
                    selectedReminderOption = ReminderQuickOption.Custom,
                    reminderInputError = null,
                ),
                dialog = null,
                bottomSheet = null,
            )
        }
    }
    
    private fun onSelectShareContacts() {
        viewModelScope.launch {
            val contacts = useCases.getCachedContacts.execute()
            _state.update { current ->
                current.copy(
                    availableContacts = contacts,
                    bottomSheet = ShareWithContacts(
                        contacts = contacts,
                        selectedContactIds = current.selectedContacts.map { it.userId }.toSet()
                    )
                )
            }
        }
    }
    
    private fun onToggleContactSelection(contactId: Long) {
        _state.update { current ->
            val currentSheet = current.bottomSheet as? ShareWithContacts ?: return@update current
            val newSelectedIds = if (contactId in currentSheet.selectedContactIds) {
                currentSheet.selectedContactIds - contactId
            } else {
                currentSheet.selectedContactIds + contactId
            }
            val newSelectedContacts = current.availableContacts.filter { it.userId in newSelectedIds }
            current.copy(
                selectedContacts = newSelectedContacts,
                bottomSheet = currentSheet.copy(selectedContactIds = newSelectedIds)
            )
        }
    }
    
    private fun onConfirmShareSelection() {
        _state.update { it.copy(bottomSheet = null) }
    }
    
    private fun onRemoveSelectedContact(contactId: Long) {
        _state.update { current ->
            val newSelectedContacts = current.selectedContacts.filter { it.userId != contactId }
            current.copy(selectedContacts = newSelectedContacts)
        }
    }
}
