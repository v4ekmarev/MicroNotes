package com.develop.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.develop.data.database.entity.ContactEntity
import com.develop.data.database.entity.NoteShareEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с шарингом заметок.
 *
 * Предоставляет методы для управления связями между заметками и контактами.
 */
@Dao
interface NoteShareDao {

    /**
     * Получить все шаринги для заметки.
     *
     * @param noteId ID заметки.
     * @return Список шарингов.
     */
    @Query("SELECT * FROM note_shares WHERE noteId = :noteId")
    suspend fun getByNoteId(noteId: Long): List<NoteShareEntity>

    /**
     * Подписаться на изменения шарингов заметки.
     *
     * @param noteId ID заметки.
     * @return Flow со списком шарингов.
     */
    @Query("SELECT * FROM note_shares WHERE noteId = :noteId")
    fun observeByNoteId(noteId: Long): Flow<List<NoteShareEntity>>

    /**
     * Получить контакты, с которыми расшарена заметка.
     *
     * @param noteId ID заметки.
     * @return Список контактов.
     */
    @Query("""
        SELECT c.* FROM contacts c
        INNER JOIN note_shares ns ON c.id = ns.contactId
        WHERE ns.noteId = :noteId
        ORDER BY c.displayName ASC, c.username ASC
    """)
    suspend fun getContactsForNote(noteId: Long): List<ContactEntity>

    /**
     * Подписаться на контакты, с которыми расшарена заметка.
     *
     * @param noteId ID заметки.
     * @return Flow со списком контактов.
     */
    @Query("""
        SELECT c.* FROM contacts c
        INNER JOIN note_shares ns ON c.id = ns.contactId
        WHERE ns.noteId = :noteId
        ORDER BY c.displayName ASC, c.username ASC
    """)
    fun observeContactsForNote(noteId: Long): Flow<List<ContactEntity>>

    /**
     * Получить несинхронизированные шаринги.
     *
     * @return Список шарингов, которые нужно отправить на сервер.
     */
    @Query("SELECT * FROM note_shares WHERE syncedWithServer = 0")
    suspend fun getUnsyncedShares(): List<NoteShareEntity>

    /**
     * Добавить шаринг.
     *
     * @param share Шаринг для добавления.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(share: NoteShareEntity)

    /**
     * Добавить несколько шарингов.
     *
     * @param shares Список шарингов для добавления.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shares: List<NoteShareEntity>)

    /**
     * Удалить шаринг.
     *
     * @param noteId ID заметки.
     * @param contactId ID контакта.
     */
    @Query("DELETE FROM note_shares WHERE noteId = :noteId AND contactId = :contactId")
    suspend fun delete(noteId: Long, contactId: Long)

    /**
     * Удалить все шаринги для заметки.
     *
     * @param noteId ID заметки.
     */
    @Query("DELETE FROM note_shares WHERE noteId = :noteId")
    suspend fun deleteAllForNote(noteId: Long)

    /**
     * Отметить шаринг как синхронизированный.
     *
     * @param noteId ID заметки.
     * @param contactId ID контакта.
     */
    @Query("UPDATE note_shares SET syncedWithServer = 1 WHERE noteId = :noteId AND contactId = :contactId")
    suspend fun markAsSynced(noteId: Long, contactId: Long)
}
