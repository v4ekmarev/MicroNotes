package com.develop.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.develop.data.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с заметками.
 *
 * Предоставляет методы для CRUD операций над заметками,
 * а также специализированные запросы для фильтрации и сортировки.
 *
 * ## Сортировка:
 * По умолчанию заметки сортируются по [NoteEntity.sortOrder] (ASC),
 * что позволяет закреплённым заметкам (с меньшим sortOrder) отображаться выше.
 *
 * ## Пример использования:
 * ```kotlin
 * // Получить все заметки
 * val notes = noteDao.getAll()
 *
 * // Подписаться на изменения
 * noteDao.observeAll().collect { notes ->
 *     updateUI(notes)
 * }
 *
 * // Получить заметки с напоминаниями
 * val notesWithReminders = noteDao.getWithReminders()
 * ```
 */
@Dao
interface NoteDao {

    /**
     * Получить все заметки, отсортированные по sortOrder (закреплённые выше).
     *
     * @return Список всех заметок.
     */
    @Query("SELECT * FROM notes ORDER BY sortOrder ASC, updatedAt DESC")
    suspend fun getAll(): List<NoteEntity>

    /**
     * Подписаться на изменения списка заметок.
     *
     * @return Flow со списком заметок.
     */
    @Query("SELECT * FROM notes ORDER BY sortOrder ASC, updatedAt DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    /**
     * Получить заметку по ID.
     *
     * @param id ID заметки.
     * @return Заметка или null, если не найдена.
     */
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): NoteEntity?

    /**
     * Получить заметки по категории.
     *
     * @param categoryId ID категории.
     * @return Список заметок в категории.
     */
    @Query("SELECT * FROM notes WHERE categoryId = :categoryId ORDER BY sortOrder ASC, updatedAt DESC")
    suspend fun getByCategoryId(categoryId: Long): List<NoteEntity>

    /**
     * Получить заметки по статусу.
     *
     * @param statusId ID статуса.
     * @return Список заметок с указанным статусом.
     */
    @Query("SELECT * FROM notes WHERE statusId = :statusId ORDER BY sortOrder ASC, updatedAt DESC")
    suspend fun getByStatusId(statusId: Long): List<NoteEntity>

    /**
     * Получить заметки с установленными напоминаниями.
     * Полезно для планирования уведомлений.
     *
     * @return Список заметок с reminderAt != null.
     */
    @Query("SELECT * FROM notes WHERE reminderAt IS NOT NULL ORDER BY reminderAt ASC")
    suspend fun getWithReminders(): List<NoteEntity>

    /**
     * Получить заметки с напоминаниями в указанном временном диапазоне.
     * Полезно для получения предстоящих напоминаний.
     *
     * @param fromMillis Начало диапазона (epoch millis UTC).
     * @param toMillis Конец диапазона (epoch millis UTC).
     * @return Список заметок с напоминаниями в диапазоне.
     */
    @Query("SELECT * FROM notes WHERE reminderAt BETWEEN :fromMillis AND :toMillis ORDER BY reminderAt ASC")
    suspend fun getWithRemindersBetween(fromMillis: Long, toMillis: Long): List<NoteEntity>

    /**
     * Вставить или обновить заметку.
     * При конфликте ID заменяет существующую заметку.
     *
     * @param note Заметка для вставки/обновления.
     * @return ID вставленной заметки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity): Long

    /**
     * Обновить существующую заметку.
     *
     * @param note Заметка с обновлёнными данными.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: NoteEntity)

    /**
     * Удалить заметку по ID.
     *
     * @param id ID заметки для удаления.
     */
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Поиск заметок по тексту (в заголовке или содержимом).
     *
     * @param query Поисковый запрос.
     * @return Список найденных заметок.
     */
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY sortOrder ASC, updatedAt DESC")
    suspend fun search(query: String): List<NoteEntity>
}
