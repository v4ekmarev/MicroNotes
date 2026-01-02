package com.develop.server.models

import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * Входящая заметка от другого пользователя.
 */
@Serializable
data class PendingShare(
    val id: Long,
    val senderId: Long,
    val senderUsername: String?,
    val senderPhone: String?,
    val title: String,
    val content: String,
    val createdAt: Instant
)

/**
 * Запрос на отправку заметки пользователю.
 */
@Serializable
data class SendNoteRequest(
    val recipientId: Long,
    val title: String,
    val content: String
)

/**
 * Запрос на отправку заметки нескольким пользователям.
 */
@Serializable
data class SendNoteToManyRequest(
    val recipientIds: List<Long>,
    val title: String,
    val content: String
)

/**
 * Результат отправки заметки.
 */
@Serializable
data class SendNoteResponse(
    val id: Long,
    val recipientId: Long,
    val recipientUsername: String?
)
