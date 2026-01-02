package com.develop.core.notification.di

import com.develop.core.notification.IosNotificationScheduler
import com.develop.core.notification.NotificationScheduler
import org.koin.dsl.module

actual val notificationModule = module {
    single<NotificationScheduler> { IosNotificationScheduler() }
}
