package com.develop.feature.note.data.repository

import com.develop.core.model.Category
import com.develop.core.model.Note
import com.develop.feature.note.domain.NoteRepository
import com.develop.data.database.dao.CategoryDao
import com.develop.data.database.dao.NoteDao
import com.develop.data.database.entity.CategoryEntity
import com.develop.data.database.entity.NoteEntity

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
) : NoteRepository {

    override suspend fun getAll(): List<Note> =
        noteDao.getAll().map { it.toDomain() }

    override suspend fun getById(id: Long): Note? =
        noteDao.getById(id)?.toDomain()

    override suspend fun save(note: Note) {
        noteDao.upsert(note.toEntity())
    }

    override suspend fun delete(id: Long) {
        noteDao.deleteById(id)
    }

    override suspend fun getCategories(): List<Category> =
        categoryDao.getAll().map { it.toDomain() }
}

private fun NoteEntity.toDomain(): Note = Note(
    id = id,
    categoryId = categoryId,
    statusId = statusId,
    title = title,
    content = content,
    sortOrder = sortOrder,
    createdAt = createdAt,
    updatedAt = updatedAt,
    reminderAt = reminderAt,
    isShared = isShared,
    senderName = senderName,
)

private fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    categoryId = categoryId,
    statusId = statusId,
    title = title,
    content = content,
    sortOrder = sortOrder,
    createdAt = createdAt,
    updatedAt = updatedAt,
    reminderAt = reminderAt,
    isShared = isShared,
    senderName = senderName,
)

private fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    title = title,
    sortOrder = sortOrder,
)
