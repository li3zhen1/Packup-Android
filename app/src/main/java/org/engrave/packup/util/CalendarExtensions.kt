package org.engrave.packup.util

import java.text.SimpleDateFormat
import java.util.*

@Suppress("NOTHING_TO_INLINE")
inline fun Long?.asCalendar() =
    if (this != null) Calendar.getInstance().apply {
        timeInMillis = this@asCalendar
    } else null

// TODO
@Suppress("NOTHING_TO_INLINE")
inline fun Calendar?.applyFormat(
    fmt: SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.CHINA
    )
): String = if (this != null) fmt.format(this.time) else "Unspecified Date."

// TODO
@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.toDetailedString(locale: Locale) =
    when (locale) {
        Locale.CHINA -> ""
        Locale.CHINESE -> ""
        Locale.PRC -> ""
        Locale.SIMPLIFIED_CHINESE -> ""
        Locale.TRADITIONAL_CHINESE -> ""
        else -> ""
    }

// TODO
@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.toShortString(locale: Locale) =
    when (locale) {
        Locale.CHINA -> ""
        Locale.CHINESE -> ""
        Locale.PRC -> ""
        Locale.SIMPLIFIED_CHINESE -> ""
        Locale.TRADITIONAL_CHINESE -> ""
        else -> ""
    }

@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getDate() = this.get(Calendar.DAY_OF_MONTH)

/**
 * @return [星期天, 星期一, ... , 星期六] ~ [1, 7]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getDayOfWeek() = this.get(Calendar.DAY_OF_WEEK)

/**
 * @return [0, ..., 11]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getMonth() = this.get(Calendar.MONTH)


@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getYear() = this.get(Calendar.YEAR)


@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getHourOfDay() = this.get(Calendar.HOUR_OF_DAY)

@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getMinuteOfHour() = this.get(Calendar.MINUTE)


@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.getWeekOfYear() = this.get(Calendar.WEEK_OF_YEAR)

@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.isLeapYear() = this.getYear().let {
    if (it % 100 == 0) it % 400 == 0
    else it % 4 == 0
}

@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.setToEndOfTomorrow() =
    this.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.HOUR_OF_DAY, 48)
    }

//@Suppress("NOTHING_TO_INLINE")
//inline fun Calendar.copy():Calendar = Calendar.getInstance().apply {
//    time = this.time
//    println(this.applyFormat())
//}


@Suppress("NOTHING_TO_INLINE")
inline fun Calendar.setToEndOfWeek(takeSaturdayAsEnd: Boolean = false): Calendar =
    this.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        when {
            takeSaturdayAsEnd -> {
                set(Calendar.DAY_OF_WEEK, 7)
                add(Calendar.HOUR_OF_DAY, 24)
            }
            get(Calendar.DAY_OF_WEEK) == 1 -> {// Sunday
                add(Calendar.HOUR_OF_DAY, 24)
            }
            else -> {
                set(Calendar.DAY_OF_WEEK, 7)
                add(Calendar.HOUR_OF_DAY, 48)
            }
        }
    }