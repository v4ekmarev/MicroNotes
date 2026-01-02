package com.develop.core.notification.di

import com.develop.core.common.Context
import com.develop.core.notification.AndroidNotificationScheduler
import com.develop.core.notification.NotificationScheduler
import org.koin.dsl.module

actual val notificationModule = module {
    single<NotificationScheduler> { AndroidNotificationScheduler(get<Context>()) }
}
