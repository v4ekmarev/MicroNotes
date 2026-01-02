/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.develop.uikit.components.LocalContainerColor
import com.develop.uikit.components.calendar.CalendarDate
import com.develop.uikit.components.calendar.CalendarModel
import com.develop.uikit.components.calendar.CalendarModelImpl
import com.develop.uikit.components.calendar.CalendarMonth
import com.develop.uikit.components.calendar.MillisecondsIn24Hours
import com.develop.uikit.components.calendar.PlatformDateFormat
import com.develop.uikit.components.calendar.defaultLocale
import com.develop.uikit.core.ExperimentalApi
import com.develop.uikit.core.InternalApi
import com.develop.uikit.core.theme.Theme

@Composable
@ExperimentalApi
fun rememberDateTimePickerState(
    initialSelectedDateMillis: Long = DateTimePickerDefaults.today.utcTimeMillis,
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24Hour: Boolean = PlatformDateFormat.is24HourFormat(defaultLocale()),
    yearRange: IntRange = DateTimePickerDefaults.YearRangeSmall,
): DateTimePickerState = rememberSaveable(
    saver = DateTimePickerState.Saver(is24Hour)
) {
    DateTimePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = yearRange,
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )
}

@Composable
@ExperimentalApi
fun DateTimePicker(
    state: DateTimePickerState,
    style: DatePickerStyle = DatePickerStyle.Wheel(),
    containerColor: Color = LocalContainerColor.current.takeOrElse {
        Theme.colorScheme.secondarySystemGroupedBackground
    },
    modifier: Modifier = Modifier
) {
    when (style) {
        is DatePickerStyle.Wheel -> DateTimePickerWheel(
            state = state,
            height = style.height,
            indicator = style.indicator ?: PickerDefaults.indicator(),
            containerColor = containerColor,
            modifier = modifier
        )

        is DatePickerStyle.Pager -> TODO("Pager datetime picker is not yet implemented")
    }
}

@Immutable
sealed interface DatePickerStyle {

    @Immutable
    class Pager : DatePickerStyle

    @Immutable
    class Wheel(
        val height: Dp = PickerDefaults.Height,
        val indicator: PickerIndicator? = null
    ) : DatePickerStyle {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Wheel
            if (height != other.height) return false
            if (indicator != other.indicator) return false
            return true
        }

        override fun hashCode(): Int {
            var result = height.hashCode()
            result = 31 * result + (indicator?.hashCode() ?: 0)
            return result
        }
    }
}

@OptIn(InternalApi::class, ExperimentalApi::class)
@Composable
private fun DateTimePickerWheel(
    state: DateTimePickerState,
    height: Dp = PickerDefaults.Height,
    indicator: PickerIndicator = PickerDefaults.indicator(),
    containerColor: Color = Theme.colorScheme.secondarySystemGroupedBackground,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(state) {
        state.isManual = false
    }

    Box(
        modifier = modifier
            .requiredHeight(height)
            .background(containerColor)
            .pickerIndicator(
                state = state.stateData.dateState,
                indicator = indicator
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.widthIn(max = PickerMaxWidth),
            horizontalArrangement = Arrangement.Center,
        ) {
            state.stateData.calendarModel.today

            val locale = defaultLocale()
            WheelPicker(
                state = state.stateData.dateState,
                items = state.stateData.days,
                height = height,
                modifier = Modifier.weight(2f),
                indicator = {},
                containerColor = containerColor,
            ) {
                PickerText(
                    text = if (it.value.utcTimeMillis == DateTimePickerDefaults.today.utcTimeMillis)
                        Today else it.value.format(
                        calendarModel = state.stateData.calendarModel,
                        skeleton = DateTimePickerDefaults.MonthWeekdayDaySkeleton,
                        locale = locale
                    ),
                    textAlign = TextAlign.End
                )
            }

            WheelPicker(
                state = state.stateData.hourState,
                items = if (state.stateData.is24Hour) Hours24 else Hours12,
                height = height,
                modifier = Modifier.width(
                    if (state.stateData.is24Hour)
                        TimePickerTokens.BlockWidth
                    else TimePickerTokens.BlockWidth * 2 / 3
                ),
                indicator = {},
                containerColor = containerColor,
            ) {
                NumberPickerText(
                    text = it,
                    textAlign = if (state.stateData.is24Hour)
                        TextAlign.Center else TextAlign.End,
                )
            }

            WheelPicker(
                state = state.stateData.minuteState,
                items = Minutes,
                height = height,
                modifier = if (state.stateData.is24Hour)
                    Modifier.weight(1f)
                else Modifier.width(TimePickerTokens.BlockWidth),
                indicator = {},
                containerColor = containerColor,
            ) {
                NumberPickerText(
                    text = it,
                    textAlign = if (state.stateData.is24Hour)
                        TextAlign.Start else TextAlign.Center,
                )
            }

            if (!state.stateData.is24Hour) {
                WheelPicker(
                    state = state.stateData.amPmState,
                    items = listOf(true, false),
                    height = height,
                    modifier = Modifier.weight(1f),
                    indicator = {},
                    containerColor = containerColor
                ) {
                    PickerText(
                        text = if (it) AmPm.first else AmPm.second,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalApi::class)
@Stable
internal class DateTimePickerStateData(
    internal val initialSelectedStartDateMillis: Long,
    initialSelectedEndDateMillis: Long?,
    initialDisplayedMonthMillis: Long = initialSelectedStartDateMillis,
    val initialDisplayedHour: Int,
    val initialDisplayedMinute: Int,
    val is24Hour: Boolean,
    val yearRange: IntRange
) {
    val calendarModel: CalendarModel = CalendarModelImpl()

    val selectedStartDate: CalendarDate by derivedStateOf(structuralEqualityPolicy()) {
        days[dateState.selectedItemIndex].value
    }

    var selectedEndDate = mutableStateOf<CalendarDate?>(null)

    val selectedMinute: Int by derivedStateOf(structuralEqualityPolicy()) {
        minuteState.selectedItemIndex
    }

    val selectedHour: Int by derivedStateOf(structuralEqualityPolicy()) {
        if (!is24Hour && isEvening)
            12 + hourState.selectedItemIndex else hourState.selectedItemIndex
    }

    private val isEvening get() = amPmState.selectedItemIndex == 1

    internal val todayIndex: Int by lazy {
        val start = calendarModel.getDate(yearRange.first, 1, 1).utcTimeMillis
        ((initialDisplayedMonthMillis - start) / MillisecondsIn24Hours).toInt()
    }

    internal val dateState by lazy {
        PickerState(initiallySelectedItemIndex = todayIndex)
    }

    internal val hourState by lazy {
        PickerState(
            infinite = true,
            initiallySelectedItemIndex = if (is24Hour)
                initialDisplayedHour else initialDisplayedHour % 12
        )
    }

    internal val minuteState by lazy {
        PickerState(
            infinite = true,
            initiallySelectedItemIndex = initialDisplayedMinute
        )
    }

    internal val amPmState by lazy {
        PickerState()
    }

    init {
        setSelection(
            startDateMillis = initialSelectedStartDateMillis,
            endDateMillis = initialSelectedEndDateMillis
        )
    }

    var displayedMonth by mutableStateOf(
        run {
            val month = calendarModel.getMonth(initialDisplayedMonthMillis)
            require(yearRange.contains(month.year)) {
                "The initial display month's year (${month.year}) is out of the years range of $yearRange."
            }
            month
        }
    )

    val currentMonth: CalendarMonth
        get() = calendarModel.getMonth(calendarModel.today)

    val displayedMonthIndex: Int
        get() = displayedMonth.indexIn(yearRange)

    val totalMonthsInRange: Int
        get() = (yearRange.last - yearRange.first + 1) * 12

    internal val days by lazy {
        val range = yearRange
        val start = calendarModel.getDate(range.first, 1, 1).utcTimeMillis
        val end = calendarModel.getDate(range.last + 1, 1, 1).utcTimeMillis

        List(((end - start) / MillisecondsIn24Hours).toInt()) {
            lazy {
                calendarModel.getCanonicalDate(start + it * MillisecondsIn24Hours)
            }
        }
    }

    internal fun setSelection(startDateMillis: Long, endDateMillis: Long?) {
        val startDate = calendarModel.getCanonicalDate(startDateMillis)
        val endDate = if (endDateMillis != null) {
            calendarModel.getCanonicalDate(endDateMillis)
        } else {
            null
        }

        startDate.let {
            require(yearRange.contains(it.year)) {
                "The provided start date year (${it.year}) is out of the years range of $yearRange."
            }
        }
        endDate?.let {
            require(yearRange.contains(it.year)) {
                "The provided end date year (${it.year}) is out of the years range of $yearRange."
            }
        }

        if (endDate != null) {
            require(startDate.utcTimeMillis <= endDate.utcTimeMillis) {
                "The provided end date appears before the start date."
            }
        }
        selectedEndDate.value = endDate
    }

    companion object {
        fun Saver(): Saver<DateTimePickerStateData, Any> = listSaver(
            save = {
                listOf(
                    it.selectedStartDate.utcTimeMillis,
                    it.selectedEndDate.value?.utcTimeMillis,
                    it.displayedMonth.startUtcTimeMillis,
                    it.selectedHour,
                    it.selectedMinute,
                    it.is24Hour,
                    it.yearRange.first,
                    it.yearRange.last
                )
            },
            restore = { value ->
                DateTimePickerStateData(
                    initialSelectedStartDateMillis = value[0] as Long,
                    initialSelectedEndDateMillis = value[1] as Long?,
                    initialDisplayedMonthMillis = value[2] as Long,
                    initialDisplayedHour = value[3] as Int,
                    initialDisplayedMinute = value[4] as Int,
                    is24Hour = value[5] as Boolean,
                    yearRange = IntRange(value[6] as Int, value[7] as Int),
                )
            }
        )
    }
}

@OptIn(InternalApi::class)
@Stable
@ExperimentalApi
class DateTimePickerState private constructor(
    internal val stateData: DateTimePickerStateData
) {
    constructor(
        @Suppress("AutoBoxing") initialSelectedDateMillis: Long,
        yearRange: IntRange,
        initialHour: Int,
        initialMinute: Int,
        is24Hour: Boolean,
    ) : this(
        DateTimePickerStateData(
            initialSelectedStartDateMillis = initialSelectedDateMillis,
            initialSelectedEndDateMillis = null,
            yearRange = yearRange,
            initialDisplayedHour = initialHour,
            initialDisplayedMinute = initialMinute,
            is24Hour = is24Hour
        )
    )

    val selectedDateTimeMillis: Long by derivedStateOf(structuralEqualityPolicy()) {
        if (isManual) {
            mSelectedDateTimeMillis
        } else {
            stateData.selectedStartDate.utcTimeMillis +
                    ((60 * selectedHour) + selectedMinute) * 60_000
        }
    }

    val selectedMinute: Int by derivedStateOf(structuralEqualityPolicy()) {
        if (isManual) {
            val start = stateData.calendarModel.getCanonicalDate(mSelectedDateTimeMillis)
            ((mSelectedDateTimeMillis - start.utcTimeMillis) % 60).toInt()
        } else {
            stateData.selectedMinute.modSign(Minutes.size)
        }
    }

    val selectedHour: Int by derivedStateOf(structuralEqualityPolicy()) {
        if (isManual) {
            val start = stateData.calendarModel.getCanonicalDate(mSelectedDateTimeMillis)
            ((mSelectedDateTimeMillis - start.utcTimeMillis) / 60).toInt()
        } else {
            stateData.selectedHour.modSign(if (stateData.is24Hour) Hours24.size else Hours12.size)
        }
    }

    private var mSelectedDateTimeMillis: Long by mutableStateOf(stateData.initialSelectedStartDateMillis)

    @InternalApi
    var isManual: Boolean by mutableStateOf(false)

    fun setSelection(@Suppress("AutoBoxing") dateMillis: Long) {
        if (isManual) {
            mSelectedDateTimeMillis = dateMillis
        } else {
            stateData.setSelection(startDateMillis = dateMillis, endDateMillis = null)
        }
    }

    companion object {
        fun Saver(is24Hour: Boolean): Saver<DateTimePickerState, *> = Saver(
            save = {
                listOf(
                    it.selectedDateTimeMillis,
                    it.selectedHour,
                    it.selectedMinute,
                    it.stateData.yearRange.first,
                    it.stateData.yearRange.last
                )
            },
            restore = { value ->
                DateTimePickerState(
                    initialSelectedDateMillis = value[0] as Long,
                    initialHour = value[1] as Int,
                    initialMinute = value[2] as Int,
                    yearRange = value[3] as Int..value[4] as Int,
                    is24Hour = is24Hour
                )
            }
        )
    }
}

@Immutable
object DateTimePickerDefaults {
    internal val today = CalendarModelImpl().today

    val YearRangeSmall: IntRange = IntRange(today.year - 1, today.year + 1)
    val YearRangeLarge: IntRange = IntRange((today.year - 30) / 10 * 10, (today.year + 30) / 10 * 10)

    const val YearMonthSkeleton: String = "yMMMM"
    const val YearAbbrMonthDaySkeleton: String = "yMMMd"
    const val YearMonthWeekdayDaySkeleton: String = "yMMMMEEEEd"
    const val MonthWeekdayDaySkeleton: String = "MMMEEd"
}

private const val Today = "Today"
