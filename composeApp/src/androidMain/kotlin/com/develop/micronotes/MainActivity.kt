package com.develop.micronotes

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val inviteUserId = parseInviteUserId(intent)
        
        setContent {
            App(application, inviteUserId)
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val inviteUserId = parseInviteUserId(intent)
        if (inviteUserId != null) {
            setContent {
                App(application, inviteUserId)
            }
        }
    }
    
    private fun parseInviteUserId(intent: Intent?): Long? {
        val data = intent?.data ?: return null
        
        // micronotes://invite/{userId}
        // host = "invite", pathSegments = ["{userId}"]
        if (data.scheme == "micronotes" && data.host == "invite") {
            return data.lastPathSegment?.toLongOrNull()
        }
        
        // https://micronotes.app/invite/{userId}
        // host = "micronotes.app", pathSegments = ["invite", "{userId}"]
        val pathSegments = data.pathSegments
        if (pathSegments != null && pathSegments.size >= 2 && pathSegments[0] == "invite") {
            return pathSegments[1].toLongOrNull()
        }
        
        return null
    }
}

@Composable
fun Dp.roundToPx(): Int = with(LocalDensity.current) { roundToPx() }

@Composable
fun SpeechRecognizerBox1(
    modifier: Modifier = Modifier,
    language: String = Locale.getDefault().toLanguageTag(), // например "ru-RU"
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    // UI state
    var isListening by remember { mutableStateOf(false) }
    var partial by remember { mutableStateOf("") }
    var finalText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var rmsDb by remember { mutableFloatStateOf(0f) }


    // Создаём/чистим SpeechRecognizer
    val recognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(recognizer) {
        onDispose {
            recognizer?.setRecognitionListener(null)
            recognizer?.destroy()
        }
    }

    fun startListening(ctx: Context, lang: String) {
        recognizer ?: run {
            errorText = "SpeechRecognizer не доступен"
            return
        }
        finalText = ""
        partial = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            // Для ru-RU: Locale.forLanguageTag("ru-RU")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500)      // минимум «говорим»
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000) // полная тишина до завершения
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }
        recognizer.startListening(intent)
        isListening = true
    }

    // Разрешение
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) startListening(context, language) else {
                isListening = false
                errorText = "Нет разрешения на микрофон"
            }
        }
    )

    fun prepareAndStart() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            errorText = "Распознавание речи недоступно на устройстве"
            return
        }
        val hasPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        startListening(context, language)
    }

    // Вешаем слушатель один раз
    LaunchedEffect(recognizer) {
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                errorText = null
                partial = ""
                rmsDb = 0f
                isListening = true
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) { rmsDb = rmsdB.coerceIn(0f, 12f) }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { /* ждём onResults / onError */ }
            override fun onError(error: Int) {
                isListening = false
                errorText = "Ошибка: $error"
            }
            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull().orEmpty()
                finalText = text
                isListening = false
            }
            override fun onPartialResults(partialResults: Bundle?) {
                partial = partialResults?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )?.firstOrNull().orEmpty()
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }



    fun stopListening() {

        recognizer?.stopListening()
        isListening = false
    }

    // ---------- UI ----------
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = if (finalText.isNotBlank()) finalText else partial,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .heightIn(min = 56.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Индикатор уровня громкости (0..12 условно)
        LinearProgressIndicator(
            progress = (rmsDb / 12f).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp))
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (isListening) stopListening() else prepareAndStart()
                },
            ) {
                Text(if (isListening) "Стоп" else "Старт")
            }
            OutlinedButton(onClick = { finalText = ""; partial = ""; errorText = null }) {
                Text("Очистить")
            }
        }

        if (errorText != null) {
            Spacer(Modifier.height(8.dp))
            Text(errorText!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Утилита: получить Activity из контекста */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview
@Composable
fun AppAndroidPreview() {
    MicroNotesApp()
}