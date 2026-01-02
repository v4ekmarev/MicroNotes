package com.develop.feature.note_list.di

import com.develop.feature.note_list.domain.usecase.NoteListUseCases
import com.develop.feature.note_list.presentation.NoteListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val noteListModule = module {
    // NoteListUseCases получает UseCase'ы из noteModule
    // noteModule должен быть загружен раньше в главном DI (Koin.kt)
    single { NoteListUseCases(get(), get(), get()) }
    
    viewModel { NoteListViewModel(useCases = get()) }
}
