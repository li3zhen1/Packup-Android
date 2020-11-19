package org.engrave.packup.component

import android.content.Context
import android.util.AttributeSet

interface IBaseView {
    fun init(context: Context)
    fun retrieveAttributes(attrs: AttributeSet)
}