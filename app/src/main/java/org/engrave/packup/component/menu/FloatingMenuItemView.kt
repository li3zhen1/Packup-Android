package org.engrave.packup.component.menu

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.isVisible
import org.engrave.packup.R
import org.engrave.packup.component.menu.FloatingMenu.Companion.DEFAULT_ITEM_CHECKABLE_BEHAVIOR
import org.engrave.packup.component.utils.ThemeUtil
import org.engrave.packup.component.view.TemplateView

internal class FloatingMenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TemplateView(context, attrs, defStyleAttr) {
    var itemCheckableBehavior: FloatingMenu.ItemCheckableBehavior = DEFAULT_ITEM_CHECKABLE_BEHAVIOR
        set(value) {
            if (field == value)
                return
            field = value

            when (itemCheckableBehavior) {
                FloatingMenu.ItemCheckableBehavior.SINGLE -> {
                    showRadioButton = true
                    showCheckBox = false
                }
                FloatingMenu.ItemCheckableBehavior.ALL -> {
                    showRadioButton = false
                    showCheckBox = true
                }
                FloatingMenu.ItemCheckableBehavior.NONE -> {
                    showRadioButton = false
                    showCheckBox = false
                }
            }

            updateViews()
        }

    private var title: String = ""
    @DrawableRes
    internal var iconResourceId: Int? = null
    private var isChecked: Boolean = false
    private var showDividerBelow: Boolean = false
    private var showRadioButton: Boolean = false
    private var showCheckBox: Boolean = false

    fun setMenuItem(popupMenuItem: FloatingMenuItem) {
        title = popupMenuItem.title
        iconResourceId = popupMenuItem.iconResourceId
        isChecked = popupMenuItem.isChecked
        showDividerBelow = popupMenuItem.showDividerBelow

        updateViews()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                setPressedState(true)
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                setPressedState(false)
            }
            MotionEvent.ACTION_CANCEL -> {
                setPressedState(false)
            }
            else -> return false
        }
        return true
    }

    // Template

    override val templateId: Int
        get() = R.layout.view_popup_menu_item

    private var iconImageView: ImageView? = null
    private var titleView: TextView? = null
    private var radioButton: RadioButton? = null
    private var checkBox: CheckBox? = null
    private var dividerView: View? = null

    override fun onTemplateLoaded() {
        super.onTemplateLoaded()

        iconImageView = findViewInTemplateById(R.id.icon)
        titleView = findViewInTemplateById(R.id.title)
        radioButton = findViewInTemplateById(R.id.radio_button)
        checkBox = findViewInTemplateById(R.id.check_box)
        dividerView = findViewInTemplateById(R.id.divider)

        updateViews()
    }

    private fun updateViews() {
        titleView?.text = title

        iconResourceId?.let { iconImageView?.setImageResource(it) }
        iconImageView?.isVisible = iconResourceId != null

        radioButton?.isVisible = showRadioButton
        checkBox?.isVisible = showCheckBox
        dividerView?.isVisible = showDividerBelow

        updateCheckedState(isChecked)
        updateAccessibilityClickAction()
    }

    private fun setPressedState(isPressed: Boolean) {
        this.isPressed = isPressed
        radioButton?.isPressed = isPressed
        checkBox?.isPressed = isPressed
    }

    private fun updateCheckedState(isChecked: Boolean) {
        radioButton?.isChecked = isChecked
        checkBox?.isChecked = isChecked

        // Update text and icon color

        if (isChecked) {
            val foregroundSelectedColor = ThemeUtil.getThemeAttrColor(context, R.attr.packupPopupMenuItemForegroundSelectedColor)
            titleView?.setTextColor(foregroundSelectedColor)
            // Using post helps ensure that the color filter is applied to the correct image in API <= Lollipop.
            iconImageView?.post {
                iconImageView?.setColorFilter(foregroundSelectedColor, PorterDuff.Mode.SRC_IN)
                iconImageView?.invalidate()
            }
        } else {
            titleView?.setTextColor(ThemeUtil.getThemeAttrColor(context, R.attr.packupPopupMenuItemTitleColor))
            iconImageView?.post {
                iconImageView?.clearColorFilter()
                iconImageView?.invalidate()
            }
        }

        // Update content description

        val checkViewType = when {
            showRadioButton -> context.getString(R.string.popup_menu_accessibility_item_radio_button)
            showCheckBox -> context.getString(R.string.popup_menu_accessibility_item_check_box)
            else -> ""
        }

        val checkedState = if (isChecked)
            context.getString(R.string.popup_menu_accessibility_item_state_checked)
        else
            context.getString(R.string.popup_menu_accessibility_item_state_not_checked)

        contentDescription = if (showRadioButton || showCheckBox)
            "$title, $checkViewType $checkedState"
        else
            title
    }

    private fun updateAccessibilityClickAction() {
        ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                val clickLabel = if (itemCheckableBehavior == FloatingMenu.ItemCheckableBehavior.NONE)
                    R.string.popup_menu_accessibility_item_select
                else
                    R.string.popup_menu_accessibility_item_toggle

                info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfo.ACTION_CLICK,
                    context.getString(clickLabel)
                ))
            }
        })
    }
}