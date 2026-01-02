package com.develop.feature.note.domain.model

import kotlin.time.Instant

/**
 * Доменная модель входящей заметки (полученной от другого пользователя).
 */
data class SharedNote(
    val id: Long,
    val senderId: Long,
    val senderUsername: String?,
    val senderPhone: String?,
    val title: String,
    val content: String,
    val createdAt: Instant
)
