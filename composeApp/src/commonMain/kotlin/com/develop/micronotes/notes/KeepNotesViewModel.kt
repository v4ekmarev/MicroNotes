package com.develop.micronotes.notes

import androidx.lifecycle.ViewModel
import com.develop.micronotes.notes.presentation.model.Note
import com.develop.micronotes.notes.presentation.data.NotesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class KeepNotesViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        NotesState(
            notesPinned = emptyList(),
            notesOthers = sampleNotes()
        )
    )
    val state: StateFlow<NotesState> = _state

    fun onQueryChange(q: String) = _state.update { it.copy(query = q) }

    fun toggleGrid() = _state.update {
        it.copy(gridColumns = if (it.gridColumns == 2) 1 else 2)
    }

    fun toggleSelect(id: String) = _state.update {
        val sel = it.selection.toMutableSet()
        if (!sel.add(id)) sel.remove(id)
        it.copy(selection = sel)
    }

    fun togglePin(note: Note) = _state.update {
        val updated = note.copy(pinned = !note.pinned)
        val pinned = it.notesPinned.toMutableList()
        val others = it.notesOthers.toMutableList()
        if (updated.pinned) {
            others.removeAll { n -> n.id == note.id }
            pinned.add(updated)
        } else {
            pinned.removeAll { n -> n.id == note.id }
            others.add(updated)
        }
        it.copy(notesPinned = pinned, notesOthers = others)
    }

    fun deleteSelected() = _state.update {
        val sel = it.selection
        it.copy(
            selection = emptySet(),
            notesPinned = it.notesPinned.filterNot { n -> sel.contains(n.id) },
            notesOthers = it.notesOthers.filterNot { n -> sel.contains(n.id) },
        )
    }

    fun setColor(id: String, color: Long) = _state.update {
        fun List<Note>.upd() = map { if (it.id == id) it.copy(color = color) else it }
        it.copy(notesPinned = it.notesPinned.upd(), notesOthers = it.notesOthers.upd())
    }

    fun addNote(note: Note) = _state.update {
        it.copy(notesOthers = listOf(note) + it.notesOthers)
    }
}

private fun sampleNotes(): List<Note> = listOf(
    Note(id = "1", title = "Идея", content = "Проверить KMP ViewModel", color = 0xFFFFF59D),
    Note(id = "2", title = "Список", content = "Мультиселект, Пин, Цвет", color = 0xFFB2EBF2),
    Note(id = "3", title = "Заметка", content = "Compose Multiplatform", color = 0xFFFFCCBC),
)
