/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.core.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.develop.uikit.core.InternalApi
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    val current = LocalHapticFeedback.current
    return remember(current) {
        UIKitHapticFeedback(current)
    }
}

@OptIn(InternalApi::class)
private class UIKitHapticFeedback(
    private val delegate: HapticFeedback
) : HapticFeedback {

    private val notificationFeedbackGenerator by lazy {
        UINotificationFeedbackGenerator()
    }

    private val selectionFeedbackGenerator by lazy {
        UISelectionFeedbackGenerator()
    }

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        when (hapticFeedbackType) {
            HapticFeedbackType.LongPress, HapticFeedbackType.TextHandleMove -> {
                delegate.performHapticFeedback(hapticFeedbackType)
            }

            HapticFeedbackTypes.SelectionChanged -> {
                selectionFeedbackGenerator.selectionChanged()
            }

            HapticFeedbackTypes.Warning,
            HapticFeedbackTypes.Success,
            HapticFeedbackTypes.Error -> {
                notificationFeedbackGenerator.notificationOccurred(
                    NotificationFeedbackMapping[hapticFeedbackType]!!
                )
            }

            HapticFeedbackTypes.ImpactLight,
            HapticFeedbackTypes.ImpactMedium,
            HapticFeedbackTypes.ImpactHeavy -> {
                UIImpactFeedbackGenerator(
                    ImpactFeedbackMapping[hapticFeedbackType]!!
                ).impactOccurred()
            }
        }
    }
}

@OptIn(InternalApi::class)
private val NotificationFeedbackMapping by lazy {
    mapOf(
        HapticFeedbackTypes.Success to UINotificationFeedbackType.UINotificationFeedbackTypeSuccess,
        HapticFeedbackTypes.Warning to UINotificationFeedbackType.UINotificationFeedbackTypeWarning,
        HapticFeedbackTypes.Error to UINotificationFeedbackType.UINotificationFeedbackTypeError,
    )
}

@OptIn(InternalApi::class)
private val ImpactFeedbackMapping by lazy {
    mapOf(
        HapticFeedbackTypes.ImpactLight to UIImpactFeedbackStyle.UIImpactFeedbackStyleLight,
        HapticFeedbackTypes.ImpactMedium to UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium,
        HapticFeedbackTypes.ImpactHeavy to UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy,
    )
}
