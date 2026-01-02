package com.develop.core.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

/**
 * BroadcastReceiver для показа уведомлений по расписанию.
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(
            AndroidNotificationScheduler.EXTRA_NOTIFICATION_ID,
            0
        )
        val title = intent.getStringExtra(AndroidNotificationScheduler.EXTRA_TITLE) ?: "Напоминание"
        val body = intent.getStringExtra(AndroidNotificationScheduler.EXTRA_BODY) ?: ""

        val notification = NotificationCompat.Builder(
            context,
            AndroidNotificationScheduler.CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }
}
