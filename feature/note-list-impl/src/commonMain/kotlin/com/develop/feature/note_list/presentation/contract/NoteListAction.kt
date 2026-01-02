package com.develop.feature.note_list.presentation.contract

sealed interface NoteListAction {

    data class QueryChange(val query: String) : NoteListAction

    data object ToggleGrid: NoteListAction

    data object DeleteSelected: NoteListAction

    data class ToggleSelect(val id: Long?) : NoteListAction
}