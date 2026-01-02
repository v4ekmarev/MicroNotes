package com.develop.micronotes.speech.compose


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.develop.micronotes.speech.SpeechCallback
import com.develop.micronotes.speech.SpeechRecognizer
import kotlinx.coroutines.launch

@Composable
fun SpeechRecognizerBox(
    modifier: Modifier = Modifier,
    languageTag: String = "ru-RU",
    autoStart: Boolean = false,
    // Для Android: сюда передай лямбду, которая откроет системный диалог разрешения из UI-слоя.
    // Если вернёт true — виджет запустит распознавание.
    onNeedMicPermission: (suspend () -> Boolean)? = null,
) {
    val scope = rememberCoroutineScope()

    // Создаём платформенный recognizer (на Android не забудь один раз вызвать initAndroidSpeechRecognizer(appContext))
    val recognizer = remember(languageTag) { SpeechRecognizer(languageTag) }

    var isRunning by remember { mutableStateOf(false) }
    var partial by remember { mutableStateOf("") }
    var finalText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    // Коллбек для платформенной реализации
    val callback = remember {
        object : SpeechCallback {
            override fun onPartial(text: String) { partial = text }
            override fun onFinal(text: String) {
                finalText = text
                partial = ""
            }
            override fun onError(code: Int, message: String?) {
                errorText = message ?: "Error $code"
            }
            override fun onStateChanged(isRunningNow: Boolean) {
                isRunning = isRunningNow
                if (!isRunningNow) partial = ""
            }
        }
    }

    fun stopListening() {
        recognizer.stop()
        isRunning = false
    }

    fun startInternal() {
        errorText = null
        finalText = ""
        partial = ""
        recognizer.start(callback)
    }

    fun startWithAuth() {
        scope.launch {
            val authorized = recognizer.requestAuthorization()
            if (authorized) {
                startInternal()
            } else {
                // На Android сюда обычно попадём, если нет RECORD_AUDIO
                if (onNeedMicPermission != null) {
                    val granted = onNeedMicPermission.invoke()
                    if (granted) startInternal()
                    else errorText = "Microphone permission is required"
                } else {
                    errorText = "Microphone permission is required"
                }
            }
        }
    }

    // Автостарт
    LaunchedEffect(autoStart, languageTag) {
        if (autoStart && !isRunning) startWithAuth()
    }

    // Чистка при уходе со экрана
    DisposableEffect(Unit) {
        onDispose { stopListening() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        // Поле вывода
        Text(
            text = when {
                finalText.isNotBlank() -> finalText
                partial.isNotBlank() -> partial
                else -> "Скажите что-нибудь…"
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.medium)
                .padding(12.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { if (isRunning) stopListening() else startWithAuth() }
            ) { Text(if (isRunning) "Стоп" else "Старт") }

            OutlinedButton(onClick = { finalText = ""; partial = ""; errorText = null }) {
                Text("Очистить")
            }
        }

        if (errorText != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = errorText!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}