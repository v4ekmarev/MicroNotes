package com.develop.feature.note_list.domain.usecase

import com.develop.feature.note.domain.usecase.GetNotesWithCategoriesUseCase
import com.develop.feature.note.domain.usecase.GetInboxUseCase
import com.develop.feature.note.domain.usecase.AcceptSharedNoteUseCase

/**
 * Контейнер для UseCase'ов фичи NoteList.
 * Используется для уменьшения количества параметров в ViewModel.
 */
data class NoteListUseCases(
    val getNotesWithCategories: GetNotesWithCategoriesUseCase,
    val getInbox: GetInboxUseCase,
    val acceptSharedNote: AcceptSharedNoteUseCase
)
