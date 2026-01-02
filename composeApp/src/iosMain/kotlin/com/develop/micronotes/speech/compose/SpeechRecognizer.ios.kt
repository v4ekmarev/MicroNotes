package com.develop.micronotes.speech.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun SpeechRecognizer(modifier: Modifier) {
    SpeechRecognizerBox(
        languageTag = "ru-RU",
        autoStart = false,
        onNeedMicPermission = null // iOS сам показывает системные диалоги
    )
}