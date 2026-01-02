package com.develop.feature.note.domain.usecase

import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.domain.model.NotesWithCategories
import com.develop.feature.note.domain.usecase.GetNotesWithCategoriesUseCase

class GetNotesWithCategoriesUseCaseImpl(
    private val repository: NoteRepository
) : GetNotesWithCategoriesUseCase {
    override suspend fun execute(): NotesWithCategories {
        val notes = repository.getAll()
        val categories = repository.getCategories()
        return NotesWithCategories(notes = notes, categories = categories)
    }
}
