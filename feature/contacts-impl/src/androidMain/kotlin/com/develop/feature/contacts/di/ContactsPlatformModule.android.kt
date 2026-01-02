package com.develop.feature.contacts.di

import com.develop.feature.contacts.data.AndroidPhoneContactsProvider
import com.develop.feature.contacts.domain.PhoneContactsProvider
import com.develop.core.common.Context as AppContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val contactsPlatformModule: Module = module {
    single<PhoneContactsProvider> { AndroidPhoneContactsProvider(get<AppContext>()) }
}
