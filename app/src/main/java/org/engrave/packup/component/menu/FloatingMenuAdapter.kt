package org.engrave.packup.component.menu

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import org.engrave.packup.R
import kotlin.math.max

internal class FloatingMenuAdapter(
    private val context: Context,
    private val items: ArrayList<FloatingMenuItem>,
    private val itemCheckableBehavior: FloatingMenu.ItemCheckableBehavior,
    private val onItemClickListener: FloatingMenuItem.OnClickListener
) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView as? FloatingMenuItemView ?: FloatingMenuItemView(context)
        val item = getItem(position)

        view.itemCheckableBehavior = itemCheckableBehavior
        view.setMenuItem(item)
        view.setOnClickListener {
            onItemClickListener.onFloatingMenuItemClicked(item)
            announceItemStateForAccessibility(item, it)
        }

        return view
    }

    fun calculateWidth(): Int {
        var maxWidth = 0
        var minWidth =
            context.resources.getDimension(R.dimen.fluentui_popup_menu_item_min_width_no_icon)
                .toInt()
        val minWidthWithIcon =
            context.resources.getDimension(R.dimen.fluentui_popup_menu_item_min_width_icon)
                .toInt()
        val listView = ListView(context)

        for (itemViewIndex in 0 until count) {
            val itemView = getView(itemViewIndex, null, listView)

            if (itemView is FloatingMenuItemView && itemView.iconResourceId != null)
                minWidth = minWidthWithIcon

            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            maxWidth = max(maxWidth, itemView.measuredWidth)
        }

        return max(minWidth, maxWidth)
    }

    private fun announceItemStateForAccessibility(item: FloatingMenuItem, itemView: View) {
        val announcementResourceId =
            when {
                itemCheckableBehavior == FloatingMenu.ItemCheckableBehavior.NONE -> R.string.popup_menu_accessibility_item_click_selected
                item.isChecked -> R.string.popup_menu_accessibility_item_click_checked
                else -> R.string.popup_menu_accessibility_item_click_unchecked
            }
        itemView.announceForAccessibility(
            context.resources.getString(
                announcementResourceId,
                item.title
            )
        )
    }

}