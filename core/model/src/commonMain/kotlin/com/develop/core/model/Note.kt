package com.develop.core.model

/**
 * Доменная модель заметки.
 *
 * Заметка — основная единица контента в приложении. Может быть привязана к категории
 * и иметь статус выполнения. Поддерживает напоминания по времени.
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
 * val note = Note(
 *     title = "Купить продукты",
 *     content = "Молоко, хлеб, яйца",
 *     categoryId = 1L,
 *     statusId = 2L, // "In Progress"
 *     reminderAt = Clock.System.now().plus(1.hours).toEpochMilliseconds()
 * )
 * ```
 *
 * @property id Уникальный идентификатор заметки. Null для новых заметок (автогенерация).
 * @property categoryId ID категории. Null — заметка без категории.
 * @property statusId ID статуса (например, "Done", "In Progress"). Null — без статуса.
 * @property title Заголовок заметки.
 * @property content Основной текст заметки.
 * @property sortOrder Порядок сортировки. Меньшее значение = выше в списке.
 * @property createdAt Время создания (epoch milliseconds UTC).
 * @property updatedAt Время последнего обновления (epoch milliseconds UTC).
 * @property reminderAt Время напоминания (epoch milliseconds UTC). Null — без напоминания.
 * @property isShared True, если заметка получена от другого пользователя.
 * @property senderName Имя отправителя (для входящих заметок).
 */
data class Note(
    val id: Long? = null,
    val categoryId: Long? = null,
    val statusId: Long? = null,
    val title: String,
    val content: String,
    val sortOrder: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val reminderAt: Long? = null,
    val isShared: Boolean = false,
    val senderName: String? = null,
)
