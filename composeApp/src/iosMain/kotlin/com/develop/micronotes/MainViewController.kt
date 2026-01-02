package com.develop.micronotes

import androidx.compose.ui.window.ComposeUIViewController
import com.develop.core.common.Context

fun MainViewController() = ComposeUIViewController { App(Context()) }