/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.develop.uikit.components.calendar

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

internal actual object PlatformDateFormat {

    actual val firstDayOfWeek: Int
        get() = (NSCalendar.currentCalendar.firstWeekday.toInt() - 1).takeIf { it > 0 } ?: 7

    actual fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String,
        locale: CalendarLocale
    ): String {
        val nsDate = NSDate.dateWithTimeIntervalSince1970(utcTimeMillis / 1000.0)
        return NSDateFormatter().apply {
            setTimeZone(TimeZone.UTC.toNSTimeZone())
            setLocale(locale)
            setDateFormat(pattern)
        }.stringFromDate(nsDate)
    }

    actual fun formatWithSkeleton(
        utcTimeMillis: Long,
        skeleton: String,
        locale: CalendarLocale
    ): String {
        val nsDate = NSDate.dateWithTimeIntervalSince1970(utcTimeMillis / 1000.0)
        return NSDateFormatter().apply {
            setTimeZone(TimeZone.UTC.toNSTimeZone())
            setLocale(locale)
            setLocalizedDateFormatFromTemplate(skeleton)
        }.stringFromDate(nsDate)
    }

    actual fun parse(
        date: String,
        pattern: String
    ): CalendarDate? {
        val nsDate = NSDateFormatter().apply {
            setTimeZone(TimeZone.UTC.toNSTimeZone())
            setDateFormat(pattern)
        }.dateFromString(date) ?: return null

        return Instant
            .fromEpochMilliseconds((nsDate.timeIntervalSince1970 * 1000).toLong())
            .toCalendarDate(TimeZone.UTC)
    }

    actual fun getDateInputFormat(locale: CalendarLocale): DateInputFormat {
        val pattern = NSDateFormatter().apply {
            setLocale(locale)
            setDateStyle(NSDateFormatterShortStyle)
        }.dateFormat
        return datePatternAsInputFormat(pattern)
    }

    @Suppress("UNCHECKED_CAST")
    actual fun weekdayNames(locale: CalendarLocale): List<Pair<String, String>> {
        val formatter = NSDateFormatter().apply {
            setLocale(locale)
        }
        val fromSundayToSaturday = formatter.standaloneWeekdaySymbols
            .zip(formatter.shortStandaloneWeekdaySymbols) as List<Pair<String, String>>
        return fromSundayToSaturday.drop(1) + fromSundayToSaturday.first()
    }

    @Suppress("UNCHECKED_CAST")
    actual fun monthsNames(locale: CalendarLocale): List<String> {
        val formatter = NSDateFormatter().apply {
            setLocale(locale)
        }
        return formatter.monthSymbols as List<String>
    }

    actual fun is24HourFormat(locale: CalendarLocale): Boolean {
        return NSDateFormatter
            .dateFormatFromTemplate("j", 0u, locale)
            ?.contains('a') == false
    }
}
