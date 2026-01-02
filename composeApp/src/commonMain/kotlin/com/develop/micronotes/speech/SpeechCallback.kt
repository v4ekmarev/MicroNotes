package com.develop.micronotes.speech

interface SpeechCallback {
    fun onPartial(text: String)
    fun onFinal(text: String)
    fun onError(code: Int, message: String?)
    fun onStateChanged(isRunning: Boolean)
}

expect class SpeechRecognizer(
    languageTag: String = "ru-RU" // можно "en-US" и т.п.
) {
    suspend fun requestAuthorization(): Boolean
    fun start(callback: SpeechCallback)
    fun stop()
    val isRunning: Boolean
}