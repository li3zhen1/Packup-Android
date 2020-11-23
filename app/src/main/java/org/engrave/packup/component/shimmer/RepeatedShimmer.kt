package org.engrave.packup.component.shimmer

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import org.engrave.packup.R
import org.engrave.packup.component.IBaseView
import kotlin.math.PI
import kotlin.math.sin

class RepeatedShimmer : FrameLayout, IBaseView {
    private var shimmer: View

    @ColorInt
    var shimmerColor: Int = 0
        set(value) {
            field = value
            shimmer.backgroundTintList = ColorStateList.valueOf(
                value
            )
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        // ToDo 可能存在内存泄漏的问题？
        val v = inflate(context, R.layout.component_shimmer, this)
        shimmer = v.findViewById(R.id.component_shimmer_indicator)
        attrs?.let { retrieveAttributes(attrs) }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = shimmer.measuredWidth.toFloat()
        ObjectAnimator.ofFloat(
            shimmer,
            "translationX",
            -width,
            width
        ).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 1200
            interpolator = Interpolator { sin(it * PI / 2).toFloat() }
            start()
        }
    }

    override fun init(context: Context) {}

    override fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RepeatedShimmer)
        shimmerColor =
            typedArray.getColor(R.styleable.RepeatedShimmer_shimmer_color, 0xff333333.toInt())
        typedArray.recycle()
    }
}