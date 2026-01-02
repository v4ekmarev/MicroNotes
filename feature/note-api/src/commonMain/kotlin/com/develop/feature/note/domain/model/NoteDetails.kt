package com.develop.feature.note.domain.model

import com.develop.core.model.Category
import com.develop.core.model.Note

/** Агрегат для экрана заметки: сама заметка (может быть null) и все доступные категории. */
public data class NoteDetails(
    val note: Note?,
    val categories: List<Category>,
)
