/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.calendar

internal expect object PlatformDateFormat {
    val firstDayOfWeek: Int

    fun weekdayNames(locale: CalendarLocale): List<Pair<String, String>>
    fun monthsNames(locale: CalendarLocale): List<String>

    fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String,
        locale: CalendarLocale
    ): String

    fun formatWithSkeleton(
        utcTimeMillis: Long,
        skeleton: String,
        locale: CalendarLocale
    ): String

    fun parse(date: String, pattern: String): CalendarDate?
    fun getDateInputFormat(locale: CalendarLocale): DateInputFormat
    fun is24HourFormat(locale: CalendarLocale): Boolean
}
