package com.develop.micronotes.notes.presentation.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val color: Long = 0xFFFFFFFF,
    val pinned: Boolean = false,
    val updatedAt: Long = 0L,
    val isChecklist: Boolean = false,
    val labels: List<String> = emptyList()
)