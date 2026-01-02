package com.develop.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность контакта в базе данных.
 *
 * Контакт — это пользователь приложения, с которым можно делиться заметками.
 * Контакты кэшируются локально для быстрого доступа и offline-режима.
 *
 * ## Связь с заметками:
 * ```
 * ┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
 * │    contacts     │         │   note_shares   │         │      notes      │
 * ├─────────────────┤         ├─────────────────┤         ├─────────────────┤
 * │ id (PK)         │◄────────│ contactId (FK)  │         │ id (PK)         │
 * │ userId          │   N:M   │ noteId (FK)     │────────►│ ...             │
 * │ username        │         └─────────────────┘         └─────────────────┘
 * │ phone           │
 * └─────────────────┘
 * ```
 *
 * @property id Уникальный идентификатор контакта в локальной БД.
 * @property userId ID пользователя на сервере (для синхронизации).
 * @property username Имя пользователя (никнейм).
 * @property phone Номер телефона (опционально).
 * @property displayName Отображаемое имя из телефонной книги (опционально).
 * @property syncedAt Время последней синхронизации (epoch milliseconds UTC).
 */
@Entity(
    tableName = "contacts",
    indices = [
        Index("userId", unique = true)
    ]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val userId: Long,

    val username: String?,

    val phone: String?,

    val displayName: String? = null,

    val syncedAt: Long,
)
