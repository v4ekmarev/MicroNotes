package com.develop.micronotes

import android.content.Intent
import com.develop.core.common.Context

actual fun shareText(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Присоединяйся к MicroNotes! $text")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Пригласить друга")
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
}
