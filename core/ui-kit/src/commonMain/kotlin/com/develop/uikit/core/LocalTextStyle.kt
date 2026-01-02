/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

/**
 * Interoperable composition local for text style.
 */
val LocalTextStyle: ProvidableCompositionLocal<TextStyle>
    @Composable
    get() = LocalTextStyleProvider.current

val LocalTextStyleProvider = staticCompositionLocalOf {
    EmptyLocalTextStyle
}

private val EmptyLocalTextStyle = compositionLocalOf {
    TextStyle()
}
