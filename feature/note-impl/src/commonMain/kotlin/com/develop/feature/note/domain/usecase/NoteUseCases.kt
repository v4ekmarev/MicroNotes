package com.develop.feature.note.domain.usecase

import com.develop.feature.note.domain.usecase.DeleteNoteUseCase
import com.develop.feature.note.domain.usecase.GetNoteDetailsUseCase
import com.develop.feature.note.domain.usecase.SaveNoteUseCase
import com.develop.feature.note.domain.usecase.GetCachedContactsUseCase
import com.develop.feature.note.domain.usecase.ShareNoteUseCase

/**
 * Контейнер для UseCase'ов фичи Note.
 * Используется для уменьшения количества параметров в ViewModel.
 */
data class NoteUseCases(
    val getNoteDetails: GetNoteDetailsUseCase,
    val saveNote: SaveNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val getCachedContacts: GetCachedContactsUseCase,
    val shareNote: ShareNoteUseCase
)
