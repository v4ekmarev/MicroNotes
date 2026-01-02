package com.develop.data.database.di

import androidx.room.RoomDatabase
import com.develop.data.database.db.NotesDatabaseDatabase
import com.develop.data.database.db.getRoomDatabase
import org.koin.dsl.module

val databaseCommonModule = module {
    single<NotesDatabaseDatabase> {
        val builder = get<RoomDatabase.Builder<NotesDatabaseDatabase>>()
        getRoomDatabase(builder)
    }
}
