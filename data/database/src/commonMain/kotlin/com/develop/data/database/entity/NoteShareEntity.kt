package com.develop.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Сущность связи заметки с контактом (шаринг).
 *
 * Представляет связь Many-to-Many между заметками и контактами.
 * Когда заметка расшарена с контактом, создаётся запись в этой таблице.
 *
 * ## Связи:
 * ```
 * ┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
 * │      notes      │         │   note_shares   │         │    contacts     │
 * ├─────────────────┤         ├─────────────────┤         ├─────────────────┤
 * │ id (PK)         │◄────────│ noteId (FK,PK)  │         │ id (PK)         │
 * │ ...             │   N:M   │ contactId(FK,PK)│────────►│ userId          │
 * └─────────────────┘         │ sharedAt        │         │ ...             │
 *                             │ syncedWithServer│         └─────────────────┘
 *                             └─────────────────┘
 * ```
 *
 * @property noteId ID заметки (часть составного первичного ключа).
 * @property contactId ID контакта в локальной БД (часть составного первичного ключа).
 * @property sharedAt Время шаринга (epoch milliseconds UTC).
 * @property syncedWithServer Флаг синхронизации с сервером.
 */
@Entity(
    tableName = "note_shares",
    primaryKeys = ["noteId", "contactId"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("noteId"),
        Index("contactId")
    ]
)
data class NoteShareEntity(
    val noteId: Long,

    val contactId: Long,

    val sharedAt: Long,

    val syncedWithServer: Boolean = false,
)
