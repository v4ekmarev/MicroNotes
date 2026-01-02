package com.develop.feature.note.domain.usecase

import com.develop.feature.note.domain.model.NoteDetails
import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.usecase.GetNoteDetailsUseCase

class GetNoteDetailsUseCaseImpl(
    private val repository: NoteRepository
) : GetNoteDetailsUseCase {
    override suspend fun execute(params: Long?): NoteDetails {
        val note = params?.let { repository.getById(it) }
        val categories = repository.getCategories()
        return NoteDetails(note = note, categories = categories)
    }
}
