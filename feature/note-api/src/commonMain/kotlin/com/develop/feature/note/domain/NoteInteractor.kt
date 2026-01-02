package com.develop.feature.note.domain

import com.develop.core.model.Note
import com.develop.feature.note.domain.model.NoteDetails
import com.develop.feature.note.domain.model.NotesWithCategories

interface NoteInteractor {
    suspend fun getAll(): List<Note>
    suspend fun getById(id: Long): Note?
    suspend fun save(note: Note)
    suspend fun delete(id: Long)
    suspend fun getNoteDetails(id: Long?): NoteDetails
    suspend fun getNotesWithCategories(): NotesWithCategories
}
