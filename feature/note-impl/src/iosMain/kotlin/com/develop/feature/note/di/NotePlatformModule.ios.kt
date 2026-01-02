package com.develop.feature.note.di

import com.develop.data.database.di.databaseIosModule
import org.koin.dsl.module
import org.koin.core.module.Module

actual val notePlatformModule: Module = module {
   includes(databaseIosModule)
}
