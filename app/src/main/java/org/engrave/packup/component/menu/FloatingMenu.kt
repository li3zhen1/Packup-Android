package org.engrave.packup.component.menu

import android.content.Context
import android.view.View
import android.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import org.engrave.packup.R


class FloatingMenu(
    private val context: Context,
    anchorView: View,
    private val items: ArrayList<FloatingMenuItem>,
    private val itemCheckableBehavior: ItemCheckableBehavior = DEFAULT_ITEM_CHECKABLE_BEHAVIOR
) : ListPopupWindow(context), FloatingMenuItem.OnClickListener {
    companion object {
        internal val DEFAULT_ITEM_CHECKABLE_BEHAVIOR = ItemCheckableBehavior.NONE
    }

    enum class ItemCheckableBehavior {
        // No items are checkable
        NONE,
        // Only one item from the group can be checked (radio buttons)
        SINGLE,
        // All items can be checked (checkboxes)
        ALL
    }

    var onItemClickListener: FloatingMenuItem.OnClickListener? = null

    private val adapter: FloatingMenuAdapter =
        FloatingMenuAdapter(context, items, itemCheckableBehavior, this)

    init {
        setAdapter(adapter)
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popup_menu_background))
        isModal = true
        width = adapter.calculateWidth()
    }

    override fun onFloatingMenuItemClicked(floatingMenuItem: FloatingMenuItem) {
        when (itemCheckableBehavior) {
            ItemCheckableBehavior.NONE -> { }
            ItemCheckableBehavior.SINGLE -> setSingleChecked(floatingMenuItem)
            ItemCheckableBehavior.ALL -> setChecked(floatingMenuItem)
        }

        onItemClickListener?.onFloatingMenuItemClicked(floatingMenuItem)
        (context as? FloatingMenuItem.OnClickListener)?.onFloatingMenuItemClicked(floatingMenuItem)

        if (itemCheckableBehavior != ItemCheckableBehavior.ALL)
            dismiss()
    }

    private fun setChecked(item: FloatingMenuItem) {
        item.isChecked = !item.isChecked
        adapter.notifyDataSetChanged()
    }

    private fun setSingleChecked(item: FloatingMenuItem) {
        items.forEach {
            it.isChecked = it == item
        }
        adapter.notifyDataSetChanged()
    }
}