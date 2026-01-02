package com.develop.feature.note_list.presentation.contract

/** Категория с заметками для экрана списка заметок. */
data class NoteListCategory(
    val id: Long,
    val title: String,
    val items: List<NoteListItem>,
)

/**
 * Упрощённая модель заметки внутри категории.
 *
 * @property id ID заметки.
 * @property title Заголовок заметки.
 * @property statusId ID статуса заметки (null — без статуса).
 * @property statusIcon Emoji-иконка статуса (null — без иконки).
 * @property statusColor Цвет статуса в ARGB (null — цвет по умолчанию).
 * @property reminderText Текст напоминания (null — без напоминания).
 * @property senderName Имя отправителя (для входящих заметок).
 * @property isShared Флаг входящей заметки с сервера.
 */
data class NoteListItem(
    val id: Long,
    val title: String,
    val statusId: Long? = null,
    val statusIcon: String? = null,
    val statusColor: Int? = null,
    val reminderText: String? = null,
    val senderName: String? = null,
    val isShared: Boolean = false,
)
