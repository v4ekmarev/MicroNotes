package com.develop.server.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

/**
 * Таблица для временного хранения заметок, отправленных другим пользователям.
 * 
 * Жизненный цикл:
 * 1. Отправитель создаёт запись (senderId → recipientId)
 * 2. Получатель забирает заметку (GET /api/inbox)
 * 3. Получатель подтверждает получение (POST /api/inbox/{id}/ack)
 * 4. Запись удаляется с сервера
 * 
 * Заметка хранится на сервере только до подтверждения получения.
 */
object PendingShares : LongIdTable("pending_shares") {
    val senderId = reference("sender_id", Users)
    val recipientId = reference("recipient_id", Users)
    val title = varchar("title", 500)
    val content = text("content")
    val createdAt = timestamp("created_at")
}
