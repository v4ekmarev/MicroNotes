package com.develop.feature.contacts.di

import com.develop.feature.contacts.data.IosPhoneContactsProvider
import com.develop.feature.contacts.domain.PhoneContactsProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val contactsPlatformModule: Module = module {
    single<PhoneContactsProvider> { IosPhoneContactsProvider() }
}
