package com.develop.feature.note.domain

import com.develop.core.model.Category
import com.develop.core.model.Note

interface NoteRepository {
    suspend fun getAll(): List<Note>
    suspend fun getById(id: Long): Note?
    suspend fun save(note: Note)
    suspend fun delete(id: Long)

    suspend fun getCategories(): List<Category>
}
