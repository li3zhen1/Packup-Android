package org.engrave.packup.util.view

import android.app.Service
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.engrave.packup.R
import org.engrave.packup.util.SimpleCountDown


typealias TextChangedHandler = (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit
typealias TextChangedResultHandler = (s: CharSequence?, start: Int, before: Int, count: Int) -> Boolean

fun EditText.setOnTextChangedListener(textChangedHandler: TextChangedHandler) =
    this.addTextChangedListener(
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangedHandler(s, start, before, count)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    )

fun EditText.requestFocusAndShowSoftKeyboard(context: Context, needAllSelected: Boolean = false) {
    this.requestFocus()
    this.requestFocusFromTouch()
    SimpleCountDown(180) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        if (needAllSelected)
            this.selectAll()
    }.start()
}

fun EditText.setJumpToOnValidate(
    ems: Int,
    next: EditText,
    validator: TextChangedResultHandler? = null
) {

    this.setOnTextChangedListener { s, start, before, count ->
        if (validator != null) {
            val valid = validator(s, start, before, count)
            if ((s?.length == ems) && (start != 0) && (count == 1))
                if (valid) {
                    background = ContextCompat.getDrawable(
                        this.context,
                        R.drawable.primaryTextFieldBackground
                    )
                    next.requestFocusAndShowSoftKeyboard(next.context)
                } else {
                    background = ContextCompat.getDrawable(
                        this.context,
                        R.drawable.textfield_focused_error
                    )
                    (context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(
                        VibrationEffect.createOneShot(300, 128)
                    )
                    selectAll()
                }
        } else {
            if ((s?.length == ems) && (start != 0) && (count == 1))
                next.requestFocusAndShowSoftKeyboard(next.context)
        }
    }
}

/**
 * 设置TextView段落间距
 *
 * @param context          上下文
 * @param tv               给谁设置段距，就传谁
 * @param content          文字内容
 * @param paragraphSpacing 请输入段落间距（单位dp）
 * @param lineSpacingExtra xml中设置的的行距（单位dp）
 */
fun TextView.setParagraphSpacing(
    content: String,
    paragraphSpacing: Int,
    lineSpacingExtra: Int
) {
    var content = content
    if (!content.contains("\n")) {
        text = content
        return
    }
    content = content.replace("\n", "\n\r")
    var previousIndex = content.indexOf("\n\r")
    //记录每个段落开始的index，第一段没有，从第二段开始
    val nextParagraphBeginIndexes: MutableList<Int> = ArrayList()
    nextParagraphBeginIndexes.add(previousIndex)
    while (previousIndex != -1) {
        val nextIndex = content.indexOf("\n\r", previousIndex + 2)
        previousIndex = nextIndex
        if (previousIndex != -1) {
            nextParagraphBeginIndexes.add(previousIndex)
        }
    }
    //获取行高（包含文字高度和行距）
    val lineHeight = lineHeight.toFloat()
    //把\r替换成透明长方形（宽:1px，高：字高+段距）
    val spanString = SpannableString(content)
    val d = ContextCompat.getDrawable(context, R.drawable.paragraph_space)
    val density = context.resources.displayMetrics.density
    //int强转部分为：行高 - 行距 + 段距
    d!!.setBounds(
        0, 0, 1,
        ((lineHeight - lineSpacingExtra * density) / 1.2 + (paragraphSpacing - lineSpacingExtra) * density).toInt()
    )
    for (index in nextParagraphBeginIndexes) {
        // \r在String中占一个index
        spanString.setSpan(ImageSpan(d), index + 1, index + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = spanString
}