package com.develop.core.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/**
 * iOS реализация планировщика уведомлений.
 * Использует UNUserNotificationCenter.
 */
class IosNotificationScheduler : NotificationScheduler {

    private val notificationCenter: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    override fun schedule(id: Long, title: String, body: String, triggerAtMillis: Long) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound)
        }

        // Вычисляем интервал до времени уведомления
        val nowMillis = (NSDate().timeIntervalSince1970 * 1000).toLong()
        val intervalSeconds = ((triggerAtMillis - nowMillis) / 1000.0).coerceAtLeast(1.0)

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = intervalSeconds,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = id.toString(),
            content = content,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to schedule notification: ${error.localizedDescription}")
            }
        }
    }

    override fun cancel(id: Long) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf(id.toString())
        )
    }
}
