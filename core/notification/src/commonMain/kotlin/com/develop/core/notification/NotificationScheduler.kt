package com.develop.core.notification

/**
 * Планировщик локальных уведомлений.
 * Позволяет запланировать уведомление на определённое время.
 */
interface NotificationScheduler {
    /**
     * Запланировать уведомление.
     *
     * @param id Уникальный идентификатор уведомления (для отмены/обновления).
     * @param title Заголовок уведомления.
     * @param body Текст уведомления.
     * @param triggerAtMillis Время показа уведомления в миллисекундах (epoch).
     */
    fun schedule(id: Long, title: String, body: String, triggerAtMillis: Long)

    /**
     * Отменить запланированное уведомление.
     *
     * @param id Идентификатор уведомления для отмены.
     */
    fun cancel(id: Long)
}
