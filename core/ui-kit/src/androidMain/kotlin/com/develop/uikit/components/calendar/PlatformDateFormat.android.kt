/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.calendar

import android.os.Build
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal actual object PlatformDateFormat {

    actual val firstDayOfWeek: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WeekFields.of(Locale.getDefault()).firstDayOfWeek.value
        } else {
            Calendar.getInstance().firstDayOfWeek
        }

    actual fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String,
        locale: CalendarLocale
    ): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern(pattern, locale)
                .withDecimalStyle(DecimalStyle.of(locale))
            Instant.ofEpochMilli(utcTimeMillis)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
                .format(formatter)
        } else {
            val sdf = SimpleDateFormat(pattern, locale)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(java.util.Date(utcTimeMillis))
        }
    }

    actual fun formatWithSkeleton(
        utcTimeMillis: Long,
        skeleton: String,
        locale: CalendarLocale
    ): String {
        val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
        return formatWithPattern(utcTimeMillis, pattern, locale)
    }

    actual fun parse(
        date: String,
        pattern: String
    ): CalendarDate? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                val localDate = LocalDate.parse(date, formatter)
                CalendarDate(
                    year = localDate.year,
                    month = localDate.month.value,
                    dayOfMonth = localDate.dayOfMonth,
                    utcTimeMillis = localDate.atTime(LocalTime.MIDNIGHT)
                        .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
                )
            } catch (pe: DateTimeParseException) {
                null
            }
        } else {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val parsedDate = sdf.parse(date) ?: return null
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                cal.time = parsedDate
                CalendarDate(
                    year = cal.get(Calendar.YEAR),
                    month = cal.get(Calendar.MONTH) + 1,
                    dayOfMonth = cal.get(Calendar.DAY_OF_MONTH),
                    utcTimeMillis = cal.timeInMillis
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    actual fun getDateInputFormat(locale: CalendarLocale): DateInputFormat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePatternAsInputFormat(
                DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                    FormatStyle.SHORT,
                    null,
                    Chronology.ofLocale(locale),
                    locale
                )
            )
        } else {
            val pattern = (java.text.DateFormat.getDateInstance(
                java.text.DateFormat.SHORT,
                locale
            ) as SimpleDateFormat).toPattern()
            datePatternAsInputFormat(pattern)
        }
    }

    actual fun weekdayNames(locale: CalendarLocale): List<Pair<String, String>> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DayOfWeek.entries.map {
                it.getDisplayName(TextStyle.FULL, locale) to
                        it.getDisplayName(TextStyle.SHORT, locale)
            }
        } else {
            val symbols = java.text.DateFormatSymbols.getInstance(locale)
            val weekdays = symbols.weekdays
            val shortWeekdays = symbols.shortWeekdays
            // Calendar.MONDAY = 2, so we need to adjust
            listOf(
                weekdays[Calendar.MONDAY] to shortWeekdays[Calendar.MONDAY],
                weekdays[Calendar.TUESDAY] to shortWeekdays[Calendar.TUESDAY],
                weekdays[Calendar.WEDNESDAY] to shortWeekdays[Calendar.WEDNESDAY],
                weekdays[Calendar.THURSDAY] to shortWeekdays[Calendar.THURSDAY],
                weekdays[Calendar.FRIDAY] to shortWeekdays[Calendar.FRIDAY],
                weekdays[Calendar.SATURDAY] to shortWeekdays[Calendar.SATURDAY],
                weekdays[Calendar.SUNDAY] to shortWeekdays[Calendar.SUNDAY],
            )
        }
    }

    actual fun monthsNames(locale: CalendarLocale): List<String> {
        val symbols = java.text.DateFormatSymbols.getInstance(locale)
        return symbols.months.take(12)
    }

    actual fun is24HourFormat(locale: CalendarLocale): Boolean {
        val pattern = DateFormat.getBestDateTimePattern(locale, "jm")
        return !pattern.contains('a')
    }
}
