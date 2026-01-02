package com.develop.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.develop.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с категориями заметок.
 *
 * Предоставляет методы для CRUD операций над категориями.
 * Категории используются для группировки заметок (например, "Важное", "Все остальное").
 */
@Dao
interface CategoryDao {

    /**
     * Получить все категории, отсортированные по [CategoryEntity.sortOrder].
     */
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    suspend fun getAll(): List<CategoryEntity>

    /**
     * Подписаться на изменения списка категорий.
     */
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    /**
     * Получить категорию по ID.
     */
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CategoryEntity?

    /**
     * Вставить или обновить категорию.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: CategoryEntity): Long

    /**
     * Удалить категорию по ID.
     */
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)
}
