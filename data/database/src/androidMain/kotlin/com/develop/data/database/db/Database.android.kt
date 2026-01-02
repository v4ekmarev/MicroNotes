package com.develop.data.database.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.develop.data.database.util.Constants.ROOM_NAME

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<NotesDatabaseDatabase> {
    val dbFile = context.getDatabasePath(ROOM_NAME)
    return Room.databaseBuilder<NotesDatabaseDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}