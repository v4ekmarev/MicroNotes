package com.develop.feature.note_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.feature.note_list.presentation.contract.NoteListAction
import com.develop.feature.note_list.presentation.contract.NoteListCategory
import com.develop.feature.note_list.presentation.contract.NoteListEffect
import com.develop.feature.note_list.presentation.contract.NoteListItem
import com.develop.feature.note_list.presentation.contract.NoteListState
import com.develop.feature.note_list.domain.usecase.NoteListUseCases
import com.develop.core.model.Category
import com.develop.core.model.Note
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class NoteListViewModel(
    private val useCases: NoteListUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(NoteListState())
    val state: StateFlow<NoteListState> = _state

    private val _effect = Channel<NoteListEffect>(capacity = Channel.BUFFERED)
    val effect: Flow<NoteListEffect> = _effect.receiveAsFlow()

    // Кэш локальных данных
    private var localNotes: List<Note> = emptyList()
    private var localCategories: List<Category> = emptyList()
    
    init {
        loadAll()
    }

    /**
     * Загружает все заметки:
     * 1. Локальные из БД (включая ранее принятые входящие)
     * 2. Новые входящие с сервера → сохраняет в БД → отправляет ACK
     * 3. Обновляет UI
     */
    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // 1. Загружаем локальные заметки
            loadLocalNotes()
            
            // 2. Загружаем входящие с сервера и сохраняем в БД
            useCases.getInbox.execute()
                .onSuccess { sharedNotes ->
                    // Сохраняем каждую входящую заметку и отправляем ACK
                    sharedNotes.forEach { sharedNote ->
                        useCases.acceptSharedNote.execute(sharedNote)
                    }
                    // Перезагружаем локальные заметки (теперь включают новые входящие)
                    loadLocalNotes()
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
    
    /**
     * Загружает локальные заметки из БД и обновляет UI.
     */
    private suspend fun loadLocalNotes() {
        val (notes, categories) = useCases.getNotesWithCategories.execute()
        localNotes = notes
        localCategories = categories
        updateCategoriesState()
    }
    
    /**
     * Обновляет список категорий из локальных данных.
     * Входящие заметки помечены isShared=true и отображаются с именем отправителя.
     */
    private fun updateCategoriesState() {
        val grouped = localCategories.map { category ->
            NoteListCategory(
                id = category.id,
                title = category.title,
                items = localNotes
                    .filter { note -> note.categoryId == category.id }
                    .map { note ->
                        NoteListItem(
                            id = note.id ?: 0L,
                            title = note.title,
                            statusId = note.statusId,
                            reminderText = note.reminderAt?.let { formatReminderText(it) },
                            senderName = note.senderName,
                            isShared = note.isShared
                        )
                    }
            )
        }
        
        _state.update { it.copy(categories = grouped) }
    }

    fun doOnAction(action: NoteListAction) {
        when (action) {
            is NoteListAction.QueryChange -> onQueryChange(action.query)
            is NoteListAction.ToggleGrid -> onToggleGrid()
            is NoteListAction.DeleteSelected -> onDeleteSelected()
            is NoteListAction.ToggleSelect -> onToggleSelect(action.id)
        }
    }

    private fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
    }

    private fun onToggleGrid() {
        _state.update {
            it.copy(gridColumns = if (it.gridColumns == 2) 1 else 2)
        }
    }

    private fun onDeleteSelected() {
        // TODO: implement delete
    }

    private fun onToggleSelect(id: Long?) {
        _state.update {
            val sel = it.selection.toMutableSet()
            it.copy(selection = sel)
        }
    }

//    fun togglePin(note: Note) = _state.update {
//        val updated = note.copy(sectionId = if (note.sectionId == "pinned") "other" else "pinned")
//        val pinned = it.notesPinned.toMutableList()
//        val others = it.notesOthers.toMutableList()
//        if (updated.sectionId == "pinned") {
//            others.removeAll { n -> n.id == note.id }
//            pinned.add(updated)
//        } else {
//            pinned.removeAll { n -> n.id == note.id }
//            others.add(updated)
//        }
//        it.copy(notesPinned = pinned, notesOthers = others)
//    }

//    fun setColor(id: String, color: Long) = _state.update {
//        fun List<Note>.upd() = map { if (it.id == id) it.copy(color = color) else it }
//        it.copy(notesPinned = it.notesPinned.upd(), notesOthers = it.notesOthers.upd())
//    }

//    fun addNote(note: Note) = _state.update {
//        it.copy(notesOthers = listOf(note) + it.notesOthers)
//    }

    @OptIn(ExperimentalTime::class)
    private fun formatReminderText(
        millis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String {
        val localDateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.monthNumber.toString().padStart(2, '0')
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$day.$month · $hour:$minute"
    }
}
