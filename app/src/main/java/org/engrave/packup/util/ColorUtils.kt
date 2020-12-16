package org.engrave.packup.util

import androidx.annotation.ColorInt
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

@Suppress("NOTHING_TO_INLINE")
inline fun calculateBlend(a1: Float, a2: Float, c1: Float, c2: Float) =
    (c1 * a1 * (1f - a2) + c2 * a2) / (a1 + a2 - a1 * a2)

@Suppress("NOTHING_TO_INLINE")
inline fun Float.scaleAndShr(bit: Int) = (this * 255).toInt() shl bit

@Suppress("NOTHING_TO_INLINE")
@ColorInt
inline fun interpolateArgb(@ColorInt a: Int, @ColorInt b: Int): Int {


    val fAlp1 = a.alpha / 255f
    val fAlp2 = b.alpha / 255f
    val fAlpBlend = fAlp1 + fAlp2 - fAlp1 * fAlp2

    val fRed1 = a.red / 255f
    val fRed2 = b.red / 255f
    val fRedBlend = calculateBlend(fAlp1, fAlp2, fRed1, fRed2)

    val fGreen1 = a.green / 255f
    val fGreen2 = b.green / 255f
    val fGreenBlend = calculateBlend(fAlp1, fAlp2, fGreen1, fGreen2)

    val fBlue1 = a.blue / 255f
    val fBlue2 = b.blue / 255f
    val fBlueBlend = calculateBlend(fAlp1, fAlp2, fBlue1, fBlue2)

    return fAlpBlend.scaleAndShr(24) +
            fRedBlend.scaleAndShr(16) +
            fGreenBlend.scaleAndShr(8) +
            fBlueBlend.scaleAndShr(0)
}

@Suppress("NOTHING_TO_INLINE")
@ColorInt
inline fun applyAlpha(@ColorInt color: Int, alpha: Float): Int {
    val alphaHex = when {
        alpha >= 1 -> 255
        alpha <= 0 -> 0
        else -> (alpha * 255).toInt()
    }
    return (color and 0x00FFFFFF) + (alphaHex shl 24)
}