package com.develop.feature.note.domain.usecase

import com.develop.core.model.Note
import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.usecase.GetAllNotesUseCase

class GetAllNotesUseCaseImpl(
    private val repository: NoteRepository
) : GetAllNotesUseCase {
    override suspend fun execute(): List<Note> = repository.getAll()
}
