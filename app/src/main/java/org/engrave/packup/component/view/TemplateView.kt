package org.engrave.packup.component.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

abstract class TemplateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun addView(child: View) {
        throw UnsupportedOperationException("addView(View) is not supported in TemplateView")
    }

    override fun addView(child: View, index: Int) {
        throw UnsupportedOperationException("addView(View, int) is not supported in TemplateView")
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        throw UnsupportedOperationException("addView(View, LayoutParams) is not supported in TemplateView")
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        throw UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in TemplateView")
    }

    override fun removeView(child: View) {
        throw UnsupportedOperationException("removeView(View) is not supported in TemplateView")
    }

    override fun removeViewAt(index: Int) {
        throw UnsupportedOperationException("removeViewAt(int) is not supported in TemplateView")
    }

    override fun removeAllViews() {
        throw UnsupportedOperationException("removeAllViews() is not supported in TemplateView")
    }

    override fun shouldDelayChildPressedState(): Boolean = false

    // Template

    protected abstract val templateId: Int
        @LayoutRes get
    protected var templateRoot: View? = null
        private set

    private var isTemplateValid: Boolean = false

    protected fun <T : View> findViewInTemplateById(@IdRes id: Int): T? {
        return templateRoot?.findViewById(id)
    }

    protected fun invalidateTemplate() {
        isTemplateValid = false
        requestLayout()
    }

    protected open fun onTemplateLoaded() {}

    protected fun reloadTemplateIfInvalid() {
        if (!isTemplateValid)
            reloadTemplate()
    }

    private fun reloadTemplate() {
        templateRoot?.let {
            removeInternalView(it)
            templateRoot = null
        }

        templateRoot = LayoutInflater.from(context).inflate(templateId, this, false)

        templateRoot?.let {
            addInternalView(it)
        }

        isTemplateValid = true

        if (templateRoot != null)
            onTemplateLoaded()
    }

    // Internal view management

    protected fun addInternalView(view: View) {
        super.addView(view, -1, view.layoutParams ?: generateDefaultLayoutParams())
    }

    protected fun removeInternalView(view: View) {
        super.removeView(view)
    }

    // Lifecycle

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        reloadTemplateIfInvalid()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        reloadTemplateIfInvalid()
    }

    // Layout

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        reloadTemplateIfInvalid()
        val templateRoot = templateRoot
        if (templateRoot == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        measureChild(templateRoot, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            resolveSizeAndState(
                templateRoot.measuredWidth,
                widthMeasureSpec,
                templateRoot.measuredState
            ),
            resolveSizeAndState(
                templateRoot.measuredHeight,
                heightMeasureSpec,
                templateRoot.measuredState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        templateRoot?.layout(0, 0, right - left, bottom - top)
    }
}