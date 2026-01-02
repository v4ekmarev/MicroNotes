package com.develop.micronotes.di

import com.develop.core.common.Context
import com.develop.core.notification.di.notificationModule
import com.develop.core.navigation.di.navigationModule
import com.develop.data.network.di.networkModule
import com.develop.feature.contacts.di.contactsModule
import com.develop.feature.note.di.noteModule
import com.develop.feature.note_list.di.noteListModule
import com.develop.feature.profile.di.profileModule
import com.develop.feature.splash.di.splashModule
import org.koin.dsl.module


fun appModule(context: Context) = module {
    single<Context> { context }
    includes(vmModule, navigationModule, networkModule, notificationModule, contactsModule, noteModule, noteListModule, profileModule, splashModule)
}