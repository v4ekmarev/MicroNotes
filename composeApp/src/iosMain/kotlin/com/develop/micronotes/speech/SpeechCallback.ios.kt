package com.develop.micronotes.speech

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDuckOthers
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive
import platform.Foundation.NSError
import platform.Foundation.NSLocale
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import kotlin.coroutines.resume

actual class SpeechRecognizer actual constructor(
    private val languageTag: String
) {
    private var audioEngine: AVAudioEngine? = null
    private var recognizer: SFSpeechRecognizer? = null
    private var request: SFSpeechAudioBufferRecognitionRequest? = null
    private var task: SFSpeechRecognitionTask? = null

    private var running = false
    actual val isRunning: Boolean get() = running

    // --- Авторизация (микрофон + speech) ---
    actual suspend fun requestAuthorization(): Boolean {
        val speechOk = suspendCancellableCoroutine<Boolean> { cont ->
            SFSpeechRecognizer.requestAuthorization { status ->
                cont.resume(status == SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized)
            }
        }
        val micOk = suspendCancellableCoroutine<Boolean> { cont ->
            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                cont.resume(granted)
            }
        }
        return speechOk && micOk
    }

    // --- Старт ---
    @OptIn(ExperimentalForeignApi::class)
    actual fun start(callback: SpeechCallback) {
        if (running) return

        // Создаём recognizer для нужного locale
        val locale = NSLocale(localeIdentifier = languageTag)
        recognizer = SFSpeechRecognizer(locale = locale)
        if (recognizer == null || recognizer?.available != true) {
            callback.onError(-1, "SFSpeechRecognizer is not available")
            return
        }

        // Настраиваем аудио-сессию
        val session = AVAudioSession.sharedInstance()
        runCatching {
            // режим записи, «измерительный» режим, приглушаем остальных
            session.setCategory(
                category = AVAudioSessionCategoryRecord,
                mode = AVAudioSessionModeMeasurement,
                options = AVAudioSessionCategoryOptionDuckOthers,
                error = null
            )
            session.setActive(true, AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation, null)
        }.onFailure {
            callback.onError(-2, "AVAudioSession error: $it")
            return
        }

        // Источник аудио
        val engine = AVAudioEngine()
        audioEngine = engine

        val req = SFSpeechAudioBufferRecognitionRequest().apply {
            shouldReportPartialResults = true
            requiresOnDeviceRecognition = false // можно true, если хочешь оффлайн
        }

        request = req

        val inputNode = engine.inputNode
        val format = inputNode.outputFormatForBus(0u)
        // Вешаем tap и отправляем буферы в запрос
        inputNode.installTapOnBus(
            bus = 0u, bufferSize = 2048u, format = format
        ) { buffer, _ ->
            buffer?.let { req.appendAudioPCMBuffer(it) }
        }

        engine.prepare()
        runCatching { engine.startAndReturnError(null) }.onFailure {
            callback.onError(-3, "AVAudioEngine start error: $it")
            cleanUp()
            return
        }

        running = true
        callback.onStateChanged(true)

        // Запускаем задачу распознавания
        task = recognizer?.recognitionTaskWithRequest(req) { result, error ->
            when {
                error != null -> {
                    callback.onError(mapSpeechError(error), error.localizedDescription)
                    stopInternal(callback) // останавливаемся при ошибке
                }

                result != null -> {
                    val text = result.bestTranscription.formattedString ?: ""
                    if (result.isFinal()) {
                        callback.onFinal(text)
                        stopInternal(callback) // завершили фразу
                    } else {
                        callback.onPartial(text)
                    }
                }

                else -> {
                    // нет ни результата ни ошибки (редко), просто ждём
                }
            }
        }
    }

    // --- Стоп ---
    actual fun stop() = stopInternal(null)

    @OptIn(ExperimentalForeignApi::class)
    private fun stopInternal(callback: SpeechCallback?) {
        if (!running) return
        running = false

        // Снимаем tap и останавливаем движок
        audioEngine?.inputNode?.removeTapOnBus(0u)
        audioEngine?.stop()
        request?.endAudio()

        task?.cancel()
        task = null
        request = null
        audioEngine = null

        // Освобождаем аудиосессию
        runCatching {
            AVAudioSession.sharedInstance()
                .setActive(false, AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation, null)
        }

        callback?.onStateChanged(false)
    }

    private fun mapSpeechError(nsError: NSError): Int {
        // SFSpeechRecognizerErrorDomain к integer-коду напрямую не мапится, вернём -100.. для единообразия
        return -100
    }

    private fun cleanUp() {
        task?.cancel()
        request = null
        task = null
        audioEngine?.stop()
        audioEngine = null
    }
}