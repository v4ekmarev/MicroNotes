package com.develop.feature.contacts.di

import com.develop.feature.contacts.data.api.ContactApi
import com.develop.feature.contacts.data.repository.ContactsRepositoryImpl
import com.develop.feature.contacts.domain.ContactsRepository
import com.develop.feature.contacts.domain.usecase.AddContactUseCase
import com.develop.feature.contacts.domain.usecase.AddContactUseCaseImpl
import com.develop.feature.contacts.domain.usecase.ContactsUseCases
import com.develop.feature.contacts.domain.usecase.FindUsersFromPhoneContactsUseCase
import com.develop.feature.contacts.domain.usecase.FindUsersFromPhoneContactsUseCaseImpl
import com.develop.feature.contacts.domain.usecase.GetAppContactsUseCase
import com.develop.feature.contacts.domain.usecase.GetAppContactsUseCaseImpl
import com.develop.feature.contacts.domain.usecase.GetInviteLinkUseCase
import com.develop.feature.contacts.domain.usecase.GetInviteLinkUseCaseImpl
import com.develop.feature.contacts.domain.usecase.GetUserByIdUseCase
import com.develop.feature.contacts.domain.usecase.GetUserByIdUseCaseImpl
import com.develop.feature.contacts.domain.usecase.RemoveContactUseCase
import com.develop.feature.contacts.domain.usecase.RemoveContactUseCaseImpl
import com.develop.feature.contacts.presentation.ContactsViewModel
import com.develop.data.database.di.databaseCommonModule
import com.develop.data.database.db.NotesDatabaseDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

expect val contactsPlatformModule: Module

val contactsModule = module {
    includes(contactsPlatformModule, databaseCommonModule)
    
    // DAO
    single { get<NotesDatabaseDatabase>().contactDao() }
    
    // API
    single { ContactApi(get()) }
    
    // Repository
    single { ContactsRepositoryImpl(get(), get()) } bind ContactsRepository::class
    
    // UseCases
    single { GetAppContactsUseCaseImpl(get()) } bind GetAppContactsUseCase::class
    single { AddContactUseCaseImpl(get()) } bind AddContactUseCase::class
    single { RemoveContactUseCaseImpl(get()) } bind RemoveContactUseCase::class
    single { FindUsersFromPhoneContactsUseCaseImpl(get()) } bind FindUsersFromPhoneContactsUseCase::class
    single { GetInviteLinkUseCaseImpl(get()) } bind GetInviteLinkUseCase::class
    single { GetUserByIdUseCaseImpl(get()) } bind GetUserByIdUseCase::class
    single { ContactsUseCases(get(), get(), get(), get(), get(), get()) }
    
    // ViewModel
    viewModel { params ->
        ContactsViewModel(
            useCases = get(),
            phoneContactsProvider = get(),
            permissionsController = params.get(),
            route = params.get()
        )
    }
}
