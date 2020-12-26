package org.engrave.packup.util.view

import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

fun setParaSpacing(text:String): SpannableString {
    val fmt = text.replace("\n", "\n\n")
    val spannableString = SpannableString(fmt)
    val matcher = Pattern.compile("\n\n").matcher(fmt)
    while (matcher.find()) {
        spannableString.setSpan(AbsoluteSizeSpan(4, true), matcher.start() + 1, matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return spannableString
}