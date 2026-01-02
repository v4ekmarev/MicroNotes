package com.develop.data.database.di

import com.develop.core.common.Context
import androidx.room.RoomDatabase
import com.develop.data.database.db.NotesDatabaseDatabase
import com.develop.data.database.db.getDatabaseBuilder
import org.koin.dsl.module

val databaseAndroidModule = module {
    single<RoomDatabase.Builder<NotesDatabaseDatabase>> {
        val appCtx = get<Context>()
        getDatabaseBuilder(appCtx)
    }
}
