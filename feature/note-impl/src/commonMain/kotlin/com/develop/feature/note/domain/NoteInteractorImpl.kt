package com.develop.feature.note.domain

import com.develop.core.model.Note
import com.develop.feature.note.domain.model.NoteDetails
import com.develop.feature.note.domain.model.NotesWithCategories

class NoteInteractorImpl(
    private val repo: NoteRepository
) : NoteInteractor {
    override suspend fun getAll(): List<Note> = repo.getAll()
    override suspend fun getById(id: Long): Note? = repo.getById(id)
    override suspend fun save(note: Note) = repo.save(note)
    override suspend fun delete(id: Long) = repo.delete(id)

    override suspend fun getNoteDetails(id: Long?): NoteDetails {
        val note = id?.let { repo.getById(it) }
        val categories = repo.getCategories()
        return NoteDetails(note = note, categories = categories)
    }

    override suspend fun getNotesWithCategories(): NotesWithCategories {
        val notes = repo.getAll()
        val categories = repo.getCategories()
        return NotesWithCategories(notes = notes, categories = categories)
    }
}
