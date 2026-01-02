package com.develop.server.database.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

/**
 * Контакты пользователя — список людей, с которыми можно шарить заметки.
 */
object Contacts : LongIdTable("contacts") {
    val userId = reference("user_id", Users) // Владелец контакта
    val contactUserId = reference("contact_user_id", Users) // Контакт
    val addedAt = timestamp("added_at")
    
    init {
        uniqueIndex(userId, contactUserId)
    }
}
