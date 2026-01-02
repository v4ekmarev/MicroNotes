package com.develop.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность заметки в базе данных.
 *
 * Заметка — основная единица контента в приложении. Может быть привязана к категории
 * и иметь статус выполнения. Поддерживает напоминания по времени.
 *
 * ## Связи:
 * - **categoryId** → [CategoryEntity] — категория/папка заметки (опционально)
 * - **statusId** → [StatusEntity] — статус выполнения (опционально)
 *
 * ## Хранение времени:
 * Все временные поля хранятся как **epoch milliseconds в UTC**.
 * Конвертация в локальное время происходит на уровне UI:
 * ```kotlin
 * val instant = Instant.fromEpochMilliseconds(createdAt)
 * val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
 * ```
 *
 * ## Сортировка:
 * Поле [sortOrder] используется для ручной сортировки заметок.
 * Закреплённые заметки (pinned) имеют меньшее значение sortOrder и отображаются выше.
 *
 * ## Пример использования:
 * ```kotlin
 * val note = NoteEntity(
 *     id = null, // автогенерация
 *     categoryId = 1L,
 *     statusId = null,
 *     title = "Купить продукты",
 *     content = "Молоко, хлеб, яйца",
 *     sortOrder = System.currentTimeMillis(),
 *     createdAt = Clock.System.now().toEpochMilliseconds(),
 *     updatedAt = Clock.System.now().toEpochMilliseconds(),
 *     reminderAt = null
 * )
 * noteDao.upsert(note)
 * ```
 *
 * @property id Уникальный идентификатор заметки (автогенерируемый).
 * @property categoryId ID категории, к которой принадлежит заметка.
 *                     Может быть null — заметка без категории.
 *                     При удалении категории устанавливается в null (ON DELETE SET NULL).
 * @property statusId ID статуса заметки (например, "Done", "In Progress").
 *                   Может быть null — заметка без статуса.
 *                   При удалении статуса устанавливается в null (ON DELETE SET NULL).
 * @property title Заголовок заметки.
 * @property content Основной текст заметки.
 * @property sortOrder Порядок сортировки. Меньшее значение = выше в списке.
 *                    Используется для закрепления (pin) и ручной сортировки.
 * @property createdAt Время создания заметки (epoch milliseconds UTC).
 * @property updatedAt Время последнего обновления (epoch milliseconds UTC).
 * @property reminderAt Время напоминания (epoch milliseconds UTC).
 *                     Может быть null, если напоминание не установлено.
 * @property isShared True, если заметка получена от другого пользователя.
 * @property senderName Имя отправителя (для входящих заметок).
 */
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = StatusEntity::class,
            parentColumns = ["id"],
            childColumns = ["statusId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categoryId"),
        Index("statusId"),
        Index("reminderAt")
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val categoryId: Long? = null,

    val statusId: Long? = null,

    val title: String,

    val content: String,

    val sortOrder: Long,

    val createdAt: Long,

    val updatedAt: Long,

    val reminderAt: Long? = null,
    
    val isShared: Boolean = false,
    
    val senderName: String? = null,
)
