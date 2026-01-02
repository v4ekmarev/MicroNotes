package com.develop.feature.note_list.presentation.contract

data class NoteListState(
    val query: String = "",
    val gridColumns: Int = 2,
    val selection: Set<String> = emptySet(),
    val categories: List<NoteListCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val selectionMode: Boolean get() = selection.isNotEmpty()
    val isGrid: Boolean get() = gridColumns > 1
}
