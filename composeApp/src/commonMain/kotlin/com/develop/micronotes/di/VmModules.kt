package com.develop.micronotes.di

import com.develop.micronotes.notes.KeepNotesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { KeepNotesViewModel() }
}

//fun appModules() = listOf(notesModule)
