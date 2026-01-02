package com.develop.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.develop.data.database.entity.StatusEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы со статусами заметок.
 *
 * Предоставляет методы для CRUD операций над статусами.
 * Статусы используются для категоризации заметок по состоянию выполнения
 * (например, "Done", "In Progress", "Waiting").
 *
 * ## Пример использования:
 * ```kotlin
 * // Получить все статусы
 * val statuses = statusDao.getAll()
 *
 * // Подписаться на изменения статусов
 * statusDao.observeAll().collect { statuses ->
 *     updateUI(statuses)
 * }
 *
 * // Создать новый статус
 * val newStatus = StatusEntity(
 *     name = "Urgent",
 *     icon = "🔥",
 *     color = 0xFFFF5722.toInt(),
 *     sortOrder = 0
 * )
 * statusDao.insert(newStatus)
 * ```
 */
@Dao
interface StatusDao {

    /**
     * Получить все статусы, отсортированные по [StatusEntity.sortOrder].
     *
     * @return Список всех статусов.
     */
    @Query("SELECT * FROM statuses ORDER BY sortOrder ASC")
    suspend fun getAll(): List<StatusEntity>

    /**
     * Подписаться на изменения списка статусов.
     * Возвращает Flow, который эмитит новый список при любом изменении в таблице.
     *
     * @return Flow со списком статусов.
     */
    @Query("SELECT * FROM statuses ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<StatusEntity>>

    /**
     * Получить статус по ID.
     *
     * @param id ID статуса.
     * @return Статус или null, если не найден.
     */
    @Query("SELECT * FROM statuses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): StatusEntity?

    /**
     * Вставить новый статус.
     * При конфликте ID заменяет существующий статус.
     *
     * @param status Статус для вставки.
     * @return ID вставленного статуса.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(status: StatusEntity): Long

    /**
     * Вставить несколько статусов (для предзаполнения).
     *
     * @param statuses Список статусов для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(statuses: List<StatusEntity>)

    /**
     * Обновить существующий статус.
     *
     * @param status Статус с обновлёнными данными.
     */
    @Update
    suspend fun update(status: StatusEntity)

    /**
     * Удалить статус по ID.
     * Заметки с этим статусом получат statusId = null (ON DELETE SET NULL).
     *
     * @param id ID статуса для удаления.
     */
    @Query("DELETE FROM statuses WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Получить количество статусов.
     * Используется для проверки, нужно ли предзаполнять таблицу.
     *
     * @return Количество статусов в таблице.
     */
    @Query("SELECT COUNT(*) FROM statuses")
    suspend fun count(): Int
}
