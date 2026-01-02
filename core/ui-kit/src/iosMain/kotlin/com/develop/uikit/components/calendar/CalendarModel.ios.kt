/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.calendar

import androidx.compose.runtime.Composable
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual typealias CalendarLocale = NSLocale

@Composable
internal actual fun defaultLocale(): CalendarLocale = NSLocale.currentLocale()

internal actual fun currentLocale(): CalendarLocale = NSLocale.currentLocale()
