package org.engrave.packup.component.indicator

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.TextView
import org.engrave.packup.R
import org.engrave.packup.data.deadline.Deadline

class DeadlineIndicator : FrameLayout {
    val deadlines: List<Deadline>? = null
    private lateinit var mCountText: TextView
    private var _displayText = ""
    private var _clickEvent = {}
    var displayText
        get() = _displayText
        set(value) {
            this._displayText = value
            if (this::mCountText.isInitialized) mCountText.text = value
        }
    var clickEvent
        get() = _clickEvent
        set(value: ()->Unit) {
            this._clickEvent = value
            if (this::mCountText.isInitialized) mCountText.setOnClickListener{
                value()
            }
        }


    fun setOnClickListener(ev: () -> Unit) {

    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val a = context.obtainStyledAttributes(attrs, R.styleable.DeadlineIndicator)
        val mView: View = inflater.inflate(R.layout.deadline_indicator, this, true)
        mCountText = mView.findViewById(R.id.deadline_indicator_count)
        mCountText.text = displayText
        mCountText.setOnClickListener{
            clickEvent()
        }
        a.recycle()
    }
}
