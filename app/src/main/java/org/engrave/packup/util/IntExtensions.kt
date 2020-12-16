package org.engrave.packup.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt


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