package com.develop.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность категории заметок в базе данных.
 *
 * Категория — это группа/папка для организации заметок. Каждая заметка может принадлежать
 * одной категории (связь One-to-Many: Category → Notes).
 *
 * ## Связь с заметками:
 * ```
 * ┌─────────────────┐         ┌─────────────────┐
 * │   categories    │         │      notes      │
 * ├─────────────────┤         ├─────────────────┤
 * │ id (PK)         │◄────────│ categoryId (FK) │
 * │ title           │   1:N   │ ...             │
 * │ sortOrder       │         └─────────────────┘
 * └─────────────────┘
 * ```
 *
 * - Одна категория может содержать много заметок (One-to-Many)
 * - Заметка ссылается на категорию через [NoteEntity.categoryId]
 * - При удалении категории у заметок categoryId становится null (ON DELETE SET NULL)
 *
 * ## Пример использования:
 * ```kotlin
 * // Получить заметки категории
 * val workNotes = noteDao.getByCategoryId(categoryId = 1L)
 *
 * // Группировка заметок по категориям
 * val categories = categoryDao.getAll()
 * val notes = noteDao.getAll()
 * categories.map { category ->
 *     NotesCategory(
 *         title = category.title,
 *         items = notes.filter { it.categoryId == category.id }
 *     )
 * }
 * ```
 *
 * @property id Уникальный идентификатор категории (автогенерируемый).
 * @property title Название категории (например, "Важное", "Все остальное", "Идеи").
 * @property sortOrder Порядок отображения категории в списке. Меньшее значение = выше.
 *
 * @see NoteEntity.categoryId
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,

    val sortOrder: Int,
)
