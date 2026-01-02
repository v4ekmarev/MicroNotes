package com.develop.data.database.di

import androidx.room.RoomDatabase
import com.develop.data.database.db.NotesDatabaseDatabase
import com.develop.data.database.db.getDatabaseBuilder
import org.koin.dsl.module

// Provides platform Room builder for iOS
val databaseIosModule = module {
    single<RoomDatabase.Builder<NotesDatabaseDatabase>> {
        getDatabaseBuilder()
    }
}
