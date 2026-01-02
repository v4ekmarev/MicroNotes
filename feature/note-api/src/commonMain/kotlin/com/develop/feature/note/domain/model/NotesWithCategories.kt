package com.develop.feature.note.domain.model

import com.develop.core.model.Category
import com.develop.core.model.Note

/** Агрегат для списка: все заметки и все категории. */
public data class NotesWithCategories(
    val notes: List<Note>,
    val categories: List<Category>,
)
