package com.develop.micronotes.speech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import java.util.Locale

// Инициализация applicationContext один раз (например, в Application.onCreate)
private object AppCtxHolder {
    @Volatile var appContext: Context? = null
}

/** Вызови из Android Application: initAndroidSpeechRecognizer(applicationContext) */
fun initAndroidSpeechRecognizer(appContext: Context) {
    AppCtxHolder.appContext = appContext.applicationContext
}

actual class SpeechRecognizer actual constructor(
    private val languageTag: String
) {
    private var recognizer: SpeechRecognizer? = null
    private var running = false
    actual val isRunning: Boolean get() = running

    private val mainHandler = Handler(Looper.getMainLooper())

    actual suspend fun requestAuthorization(): Boolean {
        val ctx = AppCtxHolder.appContext
            ?: error("initAndroidSpeechRecognizer(context) must be called before use")
        return ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun start(callback: SpeechCallback) {
        if (running) return
        val ctx = AppCtxHolder.appContext
            ?: run {
                callback.onError(-10, "Android context not initialized. Call initAndroidSpeechRecognizer().")
                return
            }

        if (!SpeechRecognizer.isRecognitionAvailable(ctx)) {
            callback.onError(-11, "Speech recognition not available")
            return
        }

        val hasMic = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasMic) {
            callback.onError(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS, "RECORD_AUDIO not granted")
            return
        }

        val sr = SpeechRecognizer.createSpeechRecognizer(ctx)
        recognizer = sr

        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                running = true
                callback.onStateChanged(true)
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                running = false
                callback.onStateChanged(false)
                callback.onError(error, humanReadable(error))
                destroyRecognizer()
            }
            override fun onResults(results: Bundle?) {
                running = false
                callback.onStateChanged(false)
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull().orEmpty()
                if (text.isNotBlank()) callback.onFinal(text)
                destroyRecognizer()
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull().orEmpty()
                if (text.isNotBlank()) callback.onPartial(text)
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val locale = runCatching { Locale.forLanguageTag(languageTag) }.getOrDefault(Locale.getDefault())
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            // немного тюнинга таймингов
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }

        mainHandler.post {
            try {
                sr.startListening(intent)
            } catch (t: Throwable) {
                running = false
                callback.onStateChanged(false)
                callback.onError(android.speech.SpeechRecognizer.ERROR_CLIENT, "startListening failed: ${t.message}")
                destroyRecognizer()
            }
        }
    }

    actual fun stop() {
        running = false
        mainHandler.post{
            recognizer?.stopListening()
            destroyRecognizer()
        }
    }

    private fun destroyRecognizer() {
        mainHandler.removeCallbacksAndMessages(null)
        recognizer?.setRecognitionListener(null)
        recognizer?.destroy()
        recognizer = null
    }

    private fun humanReadable(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio error"
        SpeechRecognizer.ERROR_CLIENT -> "Client error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
        SpeechRecognizer.ERROR_SERVER -> "Server error"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
        else -> "Unknown error ($code)"
    }
}