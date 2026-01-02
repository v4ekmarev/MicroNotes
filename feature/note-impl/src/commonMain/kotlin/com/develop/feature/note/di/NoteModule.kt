package com.develop.feature.note.di

import com.develop.feature.note.route.NoteRoute
import com.develop.feature.note.presentation.NoteViewModel
import dev.icerock.moko.permissions.PermissionsController
import com.develop.feature.note.domain.NoteRepository
import com.develop.feature.note.data.repository.NoteRepositoryImpl
import com.develop.feature.note.domain.usecase.DeleteNoteUseCase
import com.develop.feature.note.domain.usecase.DeleteNoteUseCaseImpl
import com.develop.feature.note.domain.usecase.GetNoteDetailsUseCase
import com.develop.feature.note.domain.usecase.GetNoteDetailsUseCaseImpl
import com.develop.feature.note.domain.usecase.GetNotesWithCategoriesUseCase
import com.develop.feature.note.domain.usecase.GetNotesWithCategoriesUseCaseImpl
import com.develop.feature.note.domain.usecase.SaveNoteUseCase
import com.develop.feature.note.domain.usecase.SaveNoteUseCaseImpl
import com.develop.feature.note.domain.usecase.NoteUseCases
import com.develop.feature.note.domain.usecase.GetCachedContactsUseCase
import com.develop.feature.note.domain.usecase.GetCachedContactsUseCaseImpl
import com.develop.feature.note.domain.usecase.ShareNoteUseCase
import com.develop.feature.note.domain.usecase.ShareNoteUseCaseImpl
import com.develop.feature.note.domain.usecase.GetInboxUseCase
import com.develop.feature.note.domain.usecase.GetInboxUseCaseImpl
import com.develop.feature.note.domain.usecase.AcceptSharedNoteUseCase
import com.develop.feature.note.domain.usecase.AcceptSharedNoteUseCaseImpl
import com.develop.feature.note.data.api.NoteSharingApi
import com.develop.data.database.di.databaseCommonModule
import com.develop.data.database.db.NotesDatabaseDatabase
import com.develop.data.database.dao.CategoryDao
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

expect val notePlatformModule: Module

val noteModule = module {
    includes(notePlatformModule, databaseCommonModule)

    // DAO
    single { get<NotesDatabaseDatabase>().noteDao() }
    single<CategoryDao> { get<NotesDatabaseDatabase>().categoryDao() }
    
    // API
    single { NoteSharingApi(get()) }

    // Repository
    single { NoteRepositoryImpl(get(), get()) } bind NoteRepository::class
    
    // UseCases
    single { GetNoteDetailsUseCaseImpl(get()) } bind GetNoteDetailsUseCase::class
    single { GetNotesWithCategoriesUseCaseImpl(get()) } bind GetNotesWithCategoriesUseCase::class
    single { SaveNoteUseCaseImpl(get()) } bind SaveNoteUseCase::class
    single { DeleteNoteUseCaseImpl(get()) } bind DeleteNoteUseCase::class
    single { GetCachedContactsUseCaseImpl(get()) } bind GetCachedContactsUseCase::class
    single { ShareNoteUseCaseImpl(get()) } bind ShareNoteUseCase::class
    single { GetInboxUseCaseImpl(get()) } bind GetInboxUseCase::class
    single { AcceptSharedNoteUseCaseImpl(get(), get()) } bind AcceptSharedNoteUseCase::class
    single { NoteUseCases(get(), get(), get(), get(), get()) }
    
    viewModel { params ->
        NoteViewModel(
            useCases = get(),
            notificationScheduler = get(),
            permissionsController = params.get(),
            route = params.get()
        )
    }
}
