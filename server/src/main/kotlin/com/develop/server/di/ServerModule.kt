package com.develop.server.di

import com.develop.server.repository.ContactRepository
import com.develop.server.repository.PendingShareRepository
import com.develop.server.repository.UserRepository
import org.koin.dsl.module

val serverModule = module {
    single { UserRepository() }
    single { ContactRepository() }
    single { PendingShareRepository() }
}
