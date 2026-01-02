package com.develop.micronotes.speech.compose

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CompletableDeferred

@Composable
actual fun SpeechRecognizer(modifier: Modifier) {
    val deferredHolder = remember { arrayOf<CompletableDeferred<Boolean>?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> deferredHolder[0]?.complete(granted) }

    SpeechRecognizerBox(
        languageTag = "ru-RU",
        autoStart = false,
        onNeedMicPermission = {
            val d = CompletableDeferred<Boolean>()
            deferredHolder[0] = d
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            d.await()
        }
    )
}