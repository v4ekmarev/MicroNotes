/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.core.haptic

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.develop.uikit.core.InternalApi

@Composable
expect fun rememberHapticFeedback(): HapticFeedback

/**
 * Custom haptic feedback types.
 */
@InternalApi
object HapticFeedbackTypes {
    val SelectionChanged: HapticFeedbackType = HapticFeedbackType(1001)
    val Success: HapticFeedbackType = HapticFeedbackType(2001)
    val Warning: HapticFeedbackType = HapticFeedbackType(2002)
    val Error: HapticFeedbackType = HapticFeedbackType(2003)
    val ImpactLight: HapticFeedbackType = HapticFeedbackType(3001)
    val ImpactMedium: HapticFeedbackType = HapticFeedbackType(3002)
    val ImpactHeavy: HapticFeedbackType = HapticFeedbackType(3003)
}
