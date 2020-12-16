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