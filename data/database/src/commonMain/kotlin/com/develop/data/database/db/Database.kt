package com.develop.data.database.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Создаёт и настраивает экземпляр базы данных Room.
 *
 * ## Конфигурация:
 * - Использует BundledSQLiteDriver для кроссплатформенной совместимости
 * - Запросы выполняются в Dispatchers.IO
 * - При несовместимой миграции — destructive migration (пересоздание БД)
 *
 * ## Предзаполнение:
 * При первом создании БД автоматически добавляются:
 * - Категории: "Важное", "Все остальное"
 * - Статусы: "Done" (✅), "In Progress" (🔄), "Waiting" (⏳)
 *
 * @param builder Builder для создания базы данных.
 * @return Настроенный экземпляр [NotesDatabaseDatabase].
 */
fun getRoomDatabase(
    builder: RoomDatabase.Builder<NotesDatabaseDatabase>
): NotesDatabaseDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true)// убрать перед релизом, так как нужно нормальное состояние базы
        .addCallback(object : Callback() {
            override fun onCreate(db: SQLiteConnection) {
                super.onCreate(db)
                prepopulateDatabase(db)
            }

            override fun onOpen(db: SQLiteConnection) {
                super.onOpen(db)
                // Предзаполнение при каждом открытии с INSERT OR IGNORE
                // Это безопасно, т.к. OR IGNORE не дублирует записи
                prepopulateDatabase(db)
            }
            
            private fun prepopulateDatabase(db: SQLiteConnection) {
                // Предзаполнение категорий (с явным id для OR IGNORE)
                db.execSQL("INSERT OR IGNORE INTO categories(id, title, sortOrder) VALUES(1, 'Важное', 0)")
                db.execSQL("INSERT OR IGNORE INTO categories(id, title, sortOrder) VALUES(2, 'Все остальное', 1)")
                
                // Предзаполнение статусов (с явным id для OR IGNORE)
                // Цвета в ARGB: зелёный, синий, оранжевый
                db.execSQL("INSERT OR IGNORE INTO statuses(id, name, icon, color, sortOrder) VALUES(1, 'Done', '✅', ${0xFF4CAF50.toInt()}, 0)")
                db.execSQL("INSERT OR IGNORE INTO statuses(id, name, icon, color, sortOrder) VALUES(2, 'In Progress', '🔄', ${0xFF2196F3.toInt()}, 1)")
                db.execSQL("INSERT OR IGNORE INTO statuses(id, name, icon, color, sortOrder) VALUES(3, 'Waiting', '⏳', ${0xFFFF9800.toInt()}, 2)")
            }
        })
        .build()
}