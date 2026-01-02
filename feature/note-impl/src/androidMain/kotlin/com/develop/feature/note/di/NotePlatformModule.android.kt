package com.develop.feature.note.di

import com.develop.data.database.di.databaseAndroidModule
import org.koin.core.module.Module
import org.koin.dsl.module

actual val notePlatformModule: Module = module {
    includes(databaseAndroidModule)
}
