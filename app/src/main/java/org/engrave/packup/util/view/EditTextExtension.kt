package org.engrave.packup.util.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
//            Log.e("VALIDATOR", valid.toString())
            if ((s?.length == ems) && (start != 0) && (count == 1))
                if (valid) next.requestFocusAndShowSoftKeyboard(next.context)
                else selectAll()
        } else {
            if ((s?.length == ems) && (start != 0) && (count == 1))
                next.requestFocusAndShowSoftKeyboard(next.context)
        }
    }
}