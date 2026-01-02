package com.develop.feature.note.presentation.contract

sealed interface NoteEffect {

    data object Back : NoteEffect

    data object None : NoteEffect
}
