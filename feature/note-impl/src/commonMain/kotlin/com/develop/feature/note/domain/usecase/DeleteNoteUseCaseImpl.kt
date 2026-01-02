package com.develop.feature.note.domain.usecase

import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.usecase.DeleteNoteUseCase

class DeleteNoteUseCaseImpl(
    private val repository: NoteRepository
) : DeleteNoteUseCase {
    override suspend fun execute(params: Long) = repository.delete(params)
}
