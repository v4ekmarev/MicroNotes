package com.develop.feature.note.domain.usecase

import com.develop.core.model.Note
import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.usecase.GetNoteUseCase

class GetNoteUseCaseImpl(
    private val repository: NoteRepository
) : GetNoteUseCase {
    override suspend fun execute(params: Long): Note? = repository.getById(params)
}
