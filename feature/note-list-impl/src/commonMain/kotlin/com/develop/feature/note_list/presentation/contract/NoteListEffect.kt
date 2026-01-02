package com.develop.feature.note_list.presentation.contract

sealed interface NoteListEffect {
    data class OpenNote(val id: Long?) : NoteListEffect
}
