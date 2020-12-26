package org.engrave.packup.util

import android.content.Context
import org.engrave.packup.R
import java.text.SimpleDateFormat
import java.util.*

fun fromZuluFormat(formatted: String): Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    .apply {
        set(
            formatted.slice(0..3).toInt(),
            formatted.slice(5..6).toInt() - 1,
            formatted.slice(8..9).toInt(),
            formatted.slice(11..12).toInt(),
            formatted.slice(14..15).toInt(),
            formatted.slice(17..18).toInt()
        )
        set(Calendar.MILLISECOND, 0)
    }

fun Long?.asGmtCalendar() =
    if (this != null) Calendar.getInstance(TimeZone.getTimeZone("GMT")).apply {
        timeInMillis = this@asGmtCalendar
    } else null

fun Long?.asLocalCalendar() =
    if (this != null) Calendar.getInstance().apply {
        timeInMillis = this@asLocalCalendar
    } else null


@Deprecated("Not thread-safe", replaceWith = ReplaceWith("Calendar.toGlobalizedString()"))
@Suppress("NOTHING_TO_INLINE")
inline fun Calendar?.applyFormat(
    fmt: SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.getDefault()
    )
): String = if (this != null) fmt.format(this.time) else "Unspecified Date."


fun Calendar.toGlobalizedString(
    context: Context,
    autoOmitYear: Boolean = true,
    omitTime: Boolean = false,
    omitWeek: Boolean = true
): String {
    val weekdayLiteral = " ${
        when (this.get(Calendar.DAY_OF_WEEK)) {
            1 -> context.getString(R.string.sun)
            2 -> context.getString(R.string.mon)
            3 -> context.getString(R.string.tue)
            4 -> context.getString(R.string.wed)
            5 -> context.getString(R.string.thu)
            6 -> context.getString(R.string.fri)
            7 -> context.getString(R.string.sat)
            else -> context.getString(R.string.sun)
        }
    }"
    val isSameYear = this.get(Calendar.YEAR) == Calendar.getInstance().getYear()
    val template_omit =
        context.getString(R.string.date_time_format_omitted) +
                (if (omitWeek) "" else weekdayLiteral) +
                (if (!omitTime)" %02d:%02d" else "")
    val template_complete =
        context.getString(R.string.date_time_format) +
                (if (omitWeek) "" else weekdayLiteral) +
                (if (!omitTime)" %02d:%02d" else "")


    val monthName = when (this.get(Calendar.MONTH)) {
        0 -> context.getString(R.string.month1)
        1 -> context.getString(R.string.month2)
        2 -> context.getString(R.string.month3)
        3 -> context.getString(R.string.month4)
        4 -> context.getString(R.string.month5)
        5 -> context.getString(R.string.month6)
        6 -> context.getString(R.string.month7)
        7 -> context.getString(R.string.month8)
        8 -> context.getString(R.string.month9)
        9 -> context.getString(R.string.month10)
        10 -> context.getString(R.string.month11)
        11 -> context.getString(R.string.month12)
        else -> throw Exception("Invalid month.")
    }
    return if (autoOmitYear && isSameYear) String.format(
        template_omit,
        monthName,
        this.get(Calendar.DATE),
        this.get(Calendar.HOUR_OF_DAY),
        this.get(Calendar.MINUTE)
    ) else String.format(
        template_complete,
        this.get(Calendar.YEAR),
        monthName,
        this.get(Calendar.DATE),
        this.get(Calendar.HOUR_OF_DAY),
        this.get(Calendar.MINUTE)
    )
}

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