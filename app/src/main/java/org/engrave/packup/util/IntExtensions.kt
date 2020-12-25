package org.engrave.packup.util

import android.content.Context
import android.util.TypedValue


@Suppress("NOTHING_TO_INLINE")
inline fun Int.inDp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
).toInt()

@Suppress("NOTHING_TO_INLINE")
inline fun Float.inDp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
).toInt()


//val Int.alpha :Int get()= (this shr 24) and 0x000000FF
//val Int.red :Int get()= (this shr 16) and 0x000000FF
//val Int.green :Int get()= (this shr 8) and 0x000000FF
//val Int.blue :Int get()= this and 0x000000FF


fun Int.isLeapYear(): Boolean {
    if (this.rem(400) == 0) return true
    if (this.rem(100) == 0) return false
    if (this.rem(4) == 0) return true
    return false
}

fun Int.daysInThisMonth(year: Int): Int = when (this) {
    0 -> 31
    1 -> if (year.isLeapYear()) 29 else 28
    2 -> 31
    3 -> 30
    4 -> 31
    5 -> 30
    6 -> 31
    7 -> 31
    8 -> 30
    9 -> 31
    10 -> 30
    else -> 31
}