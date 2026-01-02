package com.develop.micronotes.notes.presentation.data

import com.develop.micronotes.notes.presentation.model.Note

data class NotesState(
    val query: String = "",
    val gridColumns: Int = 2,
    val selection: Set<String> = emptySet(),
    val notesPinned: List<Note> = emptyList(),
    val notesOthers: List<Note> = emptyList()
) {
    val selectionMode: Boolean get() = selection.isNotEmpty()
    val isGrid: Boolean get() = gridColumns > 1
}