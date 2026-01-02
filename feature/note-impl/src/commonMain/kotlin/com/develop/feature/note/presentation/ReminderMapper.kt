package com.develop.feature.note.presentation

import kotlin.time.Clock
import kotlinx.datetime.DatePeriod
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

data class ReminderUiModel(
    val reminderAt: Long? = null,
    val initialHour: Int = 9,
    val initialMinute: Int = 0,
    val selectedReminderOption: ReminderQuickOption? = null,
    val reminderDateInput: String = "",
    val reminderTimeInput: String = "",
    val reminderInputError: String? = null,
)

sealed class ReminderQuickOption(
    val title: String,
    val subtitle: String,
) {
    data object TodayEvening : ReminderQuickOption(
        title = "Сегодня вечером",
        subtitle = "20:00"
    )
    data object TomorrowMorning : ReminderQuickOption(
        title = "Завтра утром",
        subtitle = "08:00"
    )
    data object Custom : ReminderQuickOption(
        title = "Выбрать дату и время",
        subtitle = ""
    )
}

internal object ReminderMapper {

    fun toUi(
        reminderAt: Long?,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): ReminderUiModel {
        if (reminderAt == null) return ReminderUiModel()
        return ReminderUiModel(
            reminderAt = reminderAt,
            initialHour = extractHour(reminderAt, timeZone),
            initialMinute = extractMinute(reminderAt, timeZone),
            reminderDateInput = formatReminderDateInput(reminderAt, timeZone),
            reminderTimeInput = formatReminderTimeInput(reminderAt, timeZone),
        )
    }

    @OptIn(ExperimentalTime::class)
    fun calculateReminderTimestamp(
        option: ReminderQuickOption,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Long {
        val localNow = now.toLocalDateTime(timeZone)
        val today = localNow.date

        val targetDateTime = when (option) {
            is ReminderQuickOption.TodayEvening -> {
                val targetTime = LocalTime(hour = 20, minute = 0)
                val todayEvening = combine(today, targetTime)
                if (localNow.time >= targetTime) {
                    val tomorrow = today.plus(DatePeriod(days = 1))
                    combine(tomorrow, targetTime)
                } else {
                    todayEvening
                }
            }

            is ReminderQuickOption.TomorrowMorning -> {
                val tomorrow = today.plus(DatePeriod(days = 1))
                val time = LocalTime(hour = 8, minute = 0)
                combine(tomorrow, time)
            }

            is ReminderQuickOption.Custom -> {
                // Custom не должен вызывать calculateReminderTimestamp
                combine(today, LocalTime(hour = 9, minute = 0))
            }
        }

        return targetDateTime.toInstant(timeZone).toEpochMilliseconds()
    }

    @OptIn(ExperimentalTime::class)
    fun formatReminderDateTime(
        millis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String {
        val localDateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
        val datePart = buildString {
            append(localDateTime.date.day.padZero())
            append('.')
            append(localDateTime.date.month.number.padZero())
            append('.')
            append(localDateTime.date.year)
        }
        val timePart = buildString {
            append(localDateTime.time.hour.padZero())
            append(':')
            append(localDateTime.time.minute.padZero())
        }
        return "$datePart, $timePart"
    }

    @OptIn(ExperimentalTime::class)
    fun formatReminderDateInput(
        millis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String {
        val localDateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
        return buildString {
            append(localDateTime.date.dayOfMonth.padZero())
            append('.')
            append(localDateTime.date.monthNumber.padZero())
            append('.')
            append(localDateTime.date.year)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun formatReminderTimeInput(
        millis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String {
        val localDateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
        return buildString {
            append(localDateTime.time.hour.padZero())
            append(':')
            append(localDateTime.time.minute.padZero())
        }
    }

    @OptIn(ExperimentalTime::class)
    fun extractHour(
        millis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Int {
        return Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).hour
    }

    @OptIn(ExperimentalTime::class)
    fun extractMinute(
        millis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Int {
        return Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).minute
    }

    /**
     * Конвертирует UTC millis даты (полночь UTC) + локальные час/минута
     * в правильный timestamp с учётом часового пояса.
     *
     * DateTimePicker возвращает: utcDateMillis + (hour * 60 + minute) * 60_000
     * где utcDateMillis — это UTC полночь выбранной даты.
     * Нам нужно интерпретировать час/минуту как локальное время.
     */
    @OptIn(ExperimentalTime::class)
    fun convertPickerResultToLocalTimestamp(
        pickerMillis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Long {
        // Извлекаем дату и время из UTC интерпретации
        val utcDateTime = Instant.fromEpochMilliseconds(pickerMillis).toLocalDateTime(TimeZone.UTC)
        // Интерпретируем как локальное время
        val localDateTime = LocalDateTime(
            year = utcDateTime.year,
            monthNumber = utcDateTime.monthNumber,
            dayOfMonth = utcDateTime.dayOfMonth,
            hour = utcDateTime.hour,
            minute = utcDateTime.minute,
        )
        return localDateTime.toInstant(timeZone).toEpochMilliseconds()
    }

    fun parseDateInput(value: String): LocalDate? {
        val parts = value.trim().split('.')
        if (parts.size != 3) return null
        val day = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val year = parts[2].toIntOrNull() ?: return null
        if (day <= 0 || month <= 0 || month > 12) return null
        return runCatching { LocalDate(year, month, day) }.getOrNull()
    }

    fun parseTimeInput(value: String): LocalTime? {
        val parts = value.trim().split(':')
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null
        return LocalTime(hour = hour, minute = minute)
    }

    @OptIn(ExperimentalTime::class)
    fun combineToEpochMillis(
        date: LocalDate,
        time: LocalTime,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Long {
        return combine(date, time).toInstant(timeZone).toEpochMilliseconds()
    }
}

private fun Int.padZero(): String = toString().padStart(2, '0')

private fun combine(date: LocalDate, time: LocalTime): LocalDateTime = LocalDateTime(
    year = date.year,
    monthNumber = date.monthNumber,
    dayOfMonth = date.dayOfMonth,
    hour = time.hour,
    minute = time.minute,
)
