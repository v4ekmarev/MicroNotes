package com.develop.data.database.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.develop.data.database.dao.CategoryDao
import com.develop.data.database.dao.ContactDao
import com.develop.data.database.dao.NoteDao
import com.develop.data.database.dao.NoteShareDao
import com.develop.data.database.dao.StatusDao
import com.develop.data.database.entity.CategoryEntity
import com.develop.data.database.entity.ContactEntity
import com.develop.data.database.entity.NoteEntity
import com.develop.data.database.entity.NoteShareEntity
import com.develop.data.database.entity.StatusEntity
import com.develop.data.database.util.Converters

/**
 * Основная база данных приложения MicroNotes.
 *
 * ## Таблицы:
 * - **notes** — заметки пользователя ([NoteEntity])
 * - **categories** — категории для группировки заметок ([CategoryEntity])
 * - **statuses** — пользовательские статусы заметок ([StatusEntity])
 *
 * ## Версионирование:
 * - v1-3: Начальные версии
 * - v4: Добавлена таблица statuses, поля statusId и reminderAt в notes, удалено isDone
 * - v5: Переименована таблица sections → categories, sectionId → categoryId, удалено selected
 * - v7: Добавлены таблицы contacts и note_shares для шаринга заметок
 * - v8: Добавлены поля isShared и senderName в notes для входящих заметок
 *
 * @see NoteDao
 * @see CategoryDao
 * @see StatusDao
 */
@Database(
    version = 8,
    entities = [NoteEntity::class, CategoryEntity::class, StatusEntity::class, ContactEntity::class, NoteShareEntity::class],
    exportSchema = true
)
@TypeConverters(Converters::class)
@ConstructedBy(NotesDatabaseConstructor::class)
abstract class NotesDatabaseDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun statusDao(): StatusDao
    abstract fun contactDao(): ContactDao
    abstract fun noteShareDao(): NoteShareDao
}

expect object NotesDatabaseConstructor : RoomDatabaseConstructor<NotesDatabaseDatabase> {
    override fun initialize(): NotesDatabaseDatabase
}
