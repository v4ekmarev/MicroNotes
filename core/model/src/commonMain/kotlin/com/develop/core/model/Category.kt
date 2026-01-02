package com.develop.core.model

/**
 * Доменная модель категории заметок.
 *
 * Категория — это группа/папка для организации заметок. Используется для визуального
 * разделения заметок на экране списка (например, "Важное", "Все остальное", "Идеи").
 *
 * ## Связь с заметками:
 * ```
 * ┌─────────────────┐         ┌─────────────────┐
 * │    Category     │         │      Note       │
 * ├─────────────────┤         ├─────────────────┤
 * │ id              │◄────────│ categoryId      │
 * │ title           │   1:N   │ ...             │
 * │ sortOrder       │         └─────────────────┘
 * └─────────────────┘
 * ```
 *
 * - Одна категория может содержать много заметок (One-to-Many)
 * - Заметка ссылается на категорию через [Note.categoryId]
 * - Если категория удалена, у заметок categoryId становится null
 *
 * ## Использование в UI:
 * ```kotlin
 * // Группировка заметок по категориям на экране списка
 * val categories = interactor.getCategories()
 * val notes = interactor.getAll()
 *
 * categories.forEach { category ->
 *     CategoryHeader(title = category.title)
 *     notes.filter { it.categoryId == category.id }.forEach { note ->
 *         NoteCard(note)
 *     }
 * }
 * ```
 *
 * @property id Уникальный идентификатор категории.
 * @property title Название категории (например, "Важное", "Все остальное", "Идеи").
 * @property sortOrder Порядок отображения категории. Меньшее значение = выше в списке.
 *
 * @see Note.categoryId
 */
data class Category(
    val id: Long,
    val title: String,
    val sortOrder: Int,
)
