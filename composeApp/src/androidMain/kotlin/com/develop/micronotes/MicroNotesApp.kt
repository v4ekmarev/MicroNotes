package com.develop.micronotes

import android.app.Application
import com.develop.micronotes.speech.initAndroidSpeechRecognizer

class MicroNotesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initAndroidSpeechRecognizer(this)
    }

}