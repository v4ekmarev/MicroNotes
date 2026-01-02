package com.develop.feature.note.domain.usecase

import com.develop.core.model.Note
import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.usecase.SaveNoteUseCase

class SaveNoteUseCaseImpl(
    private val repository: NoteRepository
) : SaveNoteUseCase {
    override suspend fun execute(params: Note) = repository.save(params)
}
