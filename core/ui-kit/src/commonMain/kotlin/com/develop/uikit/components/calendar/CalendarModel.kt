/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable

expect class CalendarLocale

@Composable
@ReadOnlyComposable
internal expect fun defaultLocale(): CalendarLocale

internal expect fun currentLocale(): CalendarLocale

internal fun formatWithSkeleton(
    utcTimeMillis: Long,
    skeleton: String,
    locale: CalendarLocale
): String = PlatformDateFormat.formatWithSkeleton(utcTimeMillis, skeleton, locale)

internal interface CalendarModel {
    val today: CalendarDate
    val firstDayOfWeek: Int
    val weekdayNames: List<Pair<String, String>>

    fun getDateInputFormat(locale: CalendarLocale): DateInputFormat
    fun getCanonicalDate(timeInMillis: Long): CalendarDate
    fun getMonth(timeInMillis: Long): CalendarMonth
    fun getMonth(date: CalendarDate): CalendarMonth
    fun getMonth(year: Int, month: Int): CalendarMonth
    fun getDate(year: Int, month: Int, day: Int): CalendarDate
    fun getDayOfWeek(date: CalendarDate): Int
    fun plusMonths(from: CalendarMonth, addedMonthsCount: Int): CalendarMonth
    fun minusMonths(from: CalendarMonth, subtractedMonthsCount: Int): CalendarMonth

    fun formatWithSkeleton(
        month: CalendarMonth,
        skeleton: String,
        locale: CalendarLocale
    ): String = formatWithSkeleton(month.startUtcTimeMillis, skeleton, locale)

    fun formatWithSkeleton(
        date: CalendarDate,
        skeleton: String,
        locale: CalendarLocale
    ): String = formatWithSkeleton(date.utcTimeMillis, skeleton, locale)

    fun formatWithPattern(utcTimeMillis: Long, pattern: String, locale: CalendarLocale): String
    fun parse(date: String, pattern: String): CalendarDate?
}

internal data class CalendarDate(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val utcTimeMillis: Long
) : Comparable<CalendarDate> {
    override operator fun compareTo(other: CalendarDate): Int =
        this.utcTimeMillis.compareTo(other.utcTimeMillis)

    fun format(
        calendarModel: CalendarModel,
        skeleton: String,
        locale: CalendarLocale
    ): String = calendarModel.formatWithSkeleton(this, skeleton, locale)
}

internal data class CalendarMonth(
    val year: Int,
    val month: Int,
    val numberOfDays: Int,
    val daysFromStartOfWeekToFirstOfMonth: Int,
    val startUtcTimeMillis: Long
) {
    val endUtcTimeMillis: Long = startUtcTimeMillis + (numberOfDays * MillisecondsIn24Hours) - 1

    fun indexIn(years: IntRange): Int {
        return (year - years.first) * 12 + month - 1
    }

    fun format(
        calendarModel: CalendarModel,
        skeleton: String,
        locale: CalendarLocale
    ): String = calendarModel.formatWithSkeleton(this, skeleton, locale)
}

@Immutable
internal data class DateInputFormat(
    val patternWithDelimiters: String,
    val delimiter: Char
) {
    val patternWithoutDelimiters: String = patternWithDelimiters.replace(delimiter.toString(), "")
}

internal fun datePatternAsInputFormat(localeFormat: String): DateInputFormat {
    val patternWithDelimiters = localeFormat.replace(Regex("[^dMy/\\-.]"), "")
        .replace(Regex("d{1,2}"), "dd")
        .replace(Regex("M{1,2}"), "MM")
        .replace(Regex("y{1,4}"), "yyyy")
        .replace("My", "M/y")
        .removeSuffix(".")

    val delimiterRegex = Regex("[/\\-.]")
    val delimiterMatchResult = delimiterRegex.find(patternWithDelimiters)
    val delimiter = delimiterMatchResult!!.groups[0]!!.value
    return DateInputFormat(
        patternWithDelimiters = patternWithDelimiters,
        delimiter = delimiter[0]
    )
}

internal const val DaysInWeek: Int = 7
internal const val MillisecondsIn24Hours = 86400000L
