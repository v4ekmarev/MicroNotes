/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.develop.uikit.core.InternalApi

@Immutable
class ColorScheme internal constructor(
    val isDark: Boolean,
    val accent: Color,
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val quaternaryLabel: Color,
    val systemFill: Color,
    val secondarySystemFill: Color,
    val tertiarySystemFill: Color,
    val quaternarySystemFill: Color,
    val placeholderText: Color,
    val separator: Color,
    val opaqueSeparator: Color,
    val link: Color,
    val systemGroupedBackground: Color,
    val secondarySystemGroupedBackground: Color,
    val tertiarySystemGroupedBackground: Color,
    val systemBackground: Color,
    val secondarySystemBackground: Color,
    val tertiarySystemBackground: Color,
) {
    fun copy(
        accent: Color = this.accent,
        label: Color = this.label,
        secondaryLabel: Color = this.secondaryLabel,
        tertiaryLabel: Color = this.tertiaryLabel,
        quaternaryLabel: Color = this.quaternaryLabel,
        systemFill: Color = this.systemFill,
        secondarySystemFill: Color = this.secondarySystemFill,
        tertiarySystemFill: Color = this.tertiarySystemFill,
        quaternarySystemFill: Color = this.quaternarySystemFill,
        placeholderText: Color = this.placeholderText,
        separator: Color = this.separator,
        opaqueSeparator: Color = this.opaqueSeparator,
        link: Color = this.link,
        systemGroupedBackground: Color = this.systemGroupedBackground,
        secondarySystemGroupedBackground: Color = this.secondarySystemGroupedBackground,
        tertiarySystemGroupedBackground: Color = this.tertiarySystemGroupedBackground,
        systemBackground: Color = this.systemBackground,
        secondarySystemBackground: Color = this.secondarySystemBackground,
        tertiarySystemBackground: Color = this.tertiarySystemBackground,
    ) = ColorScheme(
        isDark = isDark,
        accent = accent,
        label = label,
        secondaryLabel = secondaryLabel,
        tertiaryLabel = tertiaryLabel,
        quaternaryLabel = quaternaryLabel,
        systemFill = systemFill,
        secondarySystemFill = secondarySystemFill,
        tertiarySystemFill = tertiarySystemFill,
        quaternarySystemFill = quaternarySystemFill,
        placeholderText = placeholderText,
        separator = separator,
        opaqueSeparator = opaqueSeparator,
        link = link,
        systemGroupedBackground = systemGroupedBackground,
        secondarySystemGroupedBackground = secondarySystemGroupedBackground,
        tertiarySystemGroupedBackground = tertiarySystemGroupedBackground,
        systemBackground = systemBackground,
        secondarySystemBackground = secondarySystemBackground,
        tertiarySystemBackground = tertiarySystemBackground
    )
}

fun lightColorScheme(
    accent: Color = SystemColors.lightAccent,
    label: Color = SystemColors.lightLabel,
    secondaryLabel: Color = SystemColors.lightSecondaryLabel,
    tertiaryLabel: Color = SystemColors.lightTertiaryLabel,
    quaternaryLabel: Color = SystemColors.lightQuaternaryLabel,
    systemFill: Color = SystemColors.lightSystemFill,
    secondarySystemFill: Color = SystemColors.lightSecondarySystemFill,
    tertiarySystemFill: Color = SystemColors.lightTertiarySystemFill,
    quaternarySystemFill: Color = SystemColors.lightQuaternarySystemFill,
    placeholderText: Color = SystemColors.lightPlaceholderText,
    separator: Color = SystemColors.lightSeparator,
    opaqueSeparator: Color = SystemColors.lightOpaqueSeparator,
    link: Color = SystemColors.lightLink,
    systemGroupedBackground: Color = SystemColors.lightSystemGroupedBackground,
    secondarySystemGroupedBackground: Color = SystemColors.lightSecondarySystemGroupedBackground,
    tertiarySystemGroupedBackground: Color = SystemColors.lightTertiarySystemGroupedBackground,
    systemBackground: Color = SystemColors.lightSystemBackground,
    secondarySystemBackground: Color = SystemColors.lightSecondarySystemBackground,
    tertiarySystemBackground: Color = SystemColors.lightTertiarySystemBackground,
): ColorScheme = ColorScheme(
    isDark = false,
    accent = accent,
    label = label,
    secondaryLabel = secondaryLabel,
    tertiaryLabel = tertiaryLabel,
    quaternaryLabel = quaternaryLabel,
    placeholderText = placeholderText,
    systemFill = systemFill,
    secondarySystemFill = secondarySystemFill,
    tertiarySystemFill = tertiarySystemFill,
    quaternarySystemFill = quaternarySystemFill,
    separator = separator,
    opaqueSeparator = opaqueSeparator,
    link = link,
    systemGroupedBackground = systemGroupedBackground,
    secondarySystemGroupedBackground = secondarySystemGroupedBackground,
    tertiarySystemGroupedBackground = tertiarySystemGroupedBackground,
    systemBackground = systemBackground,
    secondarySystemBackground = secondarySystemBackground,
    tertiarySystemBackground = tertiarySystemBackground
)

fun darkColorScheme(
    accent: Color = SystemColors.darkAccent,
    label: Color = SystemColors.darkLabel,
    secondaryLabel: Color = SystemColors.darkSecondaryLabel,
    tertiaryLabel: Color = SystemColors.darkTertiaryLabel,
    quaternaryLabel: Color = SystemColors.darkQuaternaryLabel,
    systemFill: Color = SystemColors.darkSystemFill,
    secondarySystemFill: Color = SystemColors.darkSecondarySystemFill,
    tertiarySystemFill: Color = SystemColors.darkTertiarySystemFill,
    quaternarySystemFill: Color = SystemColors.darkQuaternarySystemFill,
    placeholderText: Color = SystemColors.darkPlaceholderText,
    separator: Color = SystemColors.darkSeparator,
    opaqueSeparator: Color = SystemColors.darkOpaqueSeparator,
    link: Color = SystemColors.darkLink,
    systemGroupedBackground: Color = SystemColors.darkSystemGroupedBackground,
    secondarySystemGroupedBackground: Color = SystemColors.darkSecondarySystemGroupedBackground,
    tertiarySystemGroupedBackground: Color = SystemColors.darkTertiarySystemGroupedBackground,
    systemBackground: Color = SystemColors.darkSystemBackground,
    secondarySystemBackground: Color = SystemColors.darkSecondarySystemBackground,
    tertiarySystemBackground: Color = SystemColors.darkTertiarySystemBackground,
): ColorScheme = ColorScheme(
    isDark = true,
    accent = accent,
    label = label,
    secondaryLabel = secondaryLabel,
    tertiaryLabel = tertiaryLabel,
    quaternaryLabel = quaternaryLabel,
    placeholderText = placeholderText,
    systemFill = systemFill,
    secondarySystemFill = secondarySystemFill,
    tertiarySystemFill = tertiarySystemFill,
    quaternarySystemFill = quaternarySystemFill,
    separator = separator,
    opaqueSeparator = opaqueSeparator,
    link = link,
    systemGroupedBackground = systemGroupedBackground,
    secondarySystemGroupedBackground = secondarySystemGroupedBackground,
    tertiarySystemGroupedBackground = tertiarySystemGroupedBackground,
    systemBackground = systemBackground,
    secondarySystemBackground = secondarySystemBackground,
    tertiarySystemBackground = tertiarySystemBackground
)

private val defaultColorScheme = lightColorScheme()

@InternalApi
val LocalColorScheme = staticCompositionLocalOf {
    defaultColorScheme
}

@Composable
@ReadOnlyComposable
internal fun isDark() = Theme.colorScheme.isDark

internal object SystemColors {
    val lightAccent: Color = Color(0xFF007AFF)
    val lightLabel: Color = Color.Black
    val lightSecondaryLabel: Color = Color(0x993C3C43)
    val lightTertiaryLabel: Color = Color(0x4C3C3C43)
    val lightQuaternaryLabel: Color = Color(0x2D3C3C43)
    val lightSystemFill: Color = Color(0x5B787880)
    val lightSecondarySystemFill: Color = Color(0x51787880)
    val lightTertiarySystemFill: Color = Color(0x3D767680)
    val lightQuaternarySystemFill: Color = Color(0x2D767680)
    val lightPlaceholderText: Color = Color(0x4C3C3C43)
    val lightSeparator: Color = Color(0x493C3C43)
    val lightOpaqueSeparator: Color = Color(0xFFC6C6C8)
    val lightLink: Color = Color(0xFF007AFF)
    val lightSystemGroupedBackground: Color = Color(0xFFF2F2F7)
    val lightSecondarySystemGroupedBackground: Color = Color.White
    val lightTertiarySystemGroupedBackground: Color = Color(0xFFF2F2F7)
    val lightSystemBackground: Color = Color.White
    val lightSecondarySystemBackground: Color = Color(0xFFF2F2F7)
    val lightTertiarySystemBackground: Color = Color.White

    val darkAccent: Color = Color(0xFF0A84FF)
    val darkLabel: Color = Color.White
    val darkSecondaryLabel: Color = Color(0x99EBEBF5)
    val darkTertiaryLabel: Color = Color(0x4CEBEBF5)
    val darkQuaternaryLabel: Color = Color(0x28EBEBF5)
    val darkSystemFill: Color = Color(0x5B787880)
    val darkSecondarySystemFill: Color = Color(0x51787880)
    val darkTertiarySystemFill: Color = Color(0x3D767680)
    val darkQuaternarySystemFill: Color = Color(0x2D767680)
    val darkPlaceholderText: Color = Color(0x4CEBEBF5)
    val darkSeparator: Color = Color(0x99545458)
    val darkOpaqueSeparator: Color = Color(0xFF38383A)
    val darkLink: Color = Color(0xFF0984FF)
    val darkSystemGroupedBackground: Color = Color.Black
    val darkSecondarySystemGroupedBackground: Color = Color(0xFF1C1C1E)
    val darkTertiarySystemGroupedBackground: Color = Color(0xFF2C2C2E)
    val darkSystemBackground: Color = Color.Black
    val darkSecondarySystemBackground: Color = Color(0xFF1C1C1E)
    val darkTertiarySystemBackground: Color = Color(0xFF2C2C2E)
}
