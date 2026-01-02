package com.develop.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.develop.data.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с контактами.
 *
 * Предоставляет методы для CRUD операций над контактами,
 * а также методы для синхронизации с сервером.
 */
@Dao
interface ContactDao {

    /**
     * Получить все контакты.
     *
     * @return Список всех контактов.
     */
    @Query("SELECT * FROM contacts ORDER BY displayName ASC, username ASC")
    suspend fun getAll(): List<ContactEntity>

    /**
     * Подписаться на изменения списка контактов.
     *
     * @return Flow со списком контактов.
     */
    @Query("SELECT * FROM contacts ORDER BY displayName ASC, username ASC")
    fun observeAll(): Flow<List<ContactEntity>>

    /**
     * Получить контакт по ID.
     *
     * @param id ID контакта в локальной БД.
     * @return Контакт или null, если не найден.
     */
    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ContactEntity?

    /**
     * Получить контакт по userId (ID на сервере).
     *
     * @param userId ID пользователя на сервере.
     * @return Контакт или null, если не найден.
     */
    @Query("SELECT * FROM contacts WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): ContactEntity?

    /**
     * Получить контакты по списку userId.
     *
     * @param userIds Список ID пользователей на сервере.
     * @return Список контактов.
     */
    @Query("SELECT * FROM contacts WHERE userId IN (:userIds)")
    suspend fun getByUserIds(userIds: List<Long>): List<ContactEntity>

    /**
     * Вставить или обновить контакт.
     * При конфликте userId заменяет существующий контакт.
     *
     * @param contact Контакт для вставки/обновления.
     * @return ID вставленного контакта.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(contact: ContactEntity): Long

    /**
     * Вставить или обновить список контактов.
     *
     * @param contacts Список контактов для вставки/обновления.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(contacts: List<ContactEntity>)

    /**
     * Удалить контакт по ID.
     *
     * @param id ID контакта для удаления.
     */
    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Удалить контакт по userId.
     *
     * @param userId ID пользователя на сервере.
     */
    @Query("DELETE FROM contacts WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)

    /**
     * Удалить все контакты (для полной пересинхронизации).
     */
    @Query("DELETE FROM contacts")
    suspend fun deleteAll()

    /**
     * Поиск контактов по имени или телефону.
     *
     * @param query Поисковый запрос.
     * @return Список найденных контактов.
     */
    @Query("SELECT * FROM contacts WHERE username LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY displayName ASC, username ASC")
    suspend fun search(query: String): List<ContactEntity>
}
