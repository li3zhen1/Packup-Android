package org.engrave.packup.ui.deadline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.engrave.packup.R
import org.engrave.packup.util.applyAlpha
import org.engrave.packup.util.inDp
import org.engrave.packup.util.interpolateArgb
import kotlin.math.absoluteValue


class DeadlineItemTouchHelper(
    private val adapter: IDeadlineItemTouchHelperAdapter,
    private val context: Context
) : ItemTouchHelper.Callback() {

    val Dp24 = 24.inDp(context)

    interface IDeadlineItemTouchHelperAdapter {
        fun onItemRemoved(position: Int)
        fun onItemCompleted(position: Int)
    }

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val upFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(upFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false//never called

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == 1 shl 4)
            adapter.onItemRemoved(viewHolder.adapterPosition)
        else adapter.onItemCompleted(viewHolder.adapterPosition)
    }

    val primaryColor400 = ContextCompat.getColor(context, R.color.color_primary_400)
    val primaryColor200 = ContextCompat.getColor(context, R.color.color_primary_200)
    val vibrantColor400 = ContextCompat.getColor(context, R.color.color_vibrant_400)
    val vibrantColor200 = ContextCompat.getColor(context, R.color.color_vibrant_200)

    @ColorInt
    val backgroundColor = 0x18888888
    @ColorInt
    val iconDeactivatedColor = ContextCompat.getColor(context, R.color.colorText)

    val maxOpacity = 0.3F

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val drawableDelete =
            ContextCompat.getDrawable(context, R.drawable.ic_fluent_delete_24_regular)
        val drawableComplete =
            ContextCompat.getDrawable(context, R.drawable.ic_fluent_checkmark_circle_24_regular)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView: View = viewHolder.itemView
            val p = Paint()
            val bg = Paint()
            val k = 6
            if (viewHolder is DeadlineListAdapter.DeadlineMemberViewHolder) {
                val middleY: Int = (itemView.top + itemView.bottom) / 2

                bg.color = if (dX > 0) primaryColor400 else vibrantColor400
                val _dist = (dX / (itemView.right - itemView.left)).absoluteValue
                val _opacity = (k * _dist - 0.5 * (k - 1))
                p.color = interpolateArgb(
                    backgroundColor,
                    applyAlpha(
                        if (dX > 0) primaryColor200 else vibrantColor200,
                        when {
                            _opacity >= 1 -> maxOpacity
                            _opacity <= 0 -> 0F
                            else -> (_opacity * maxOpacity).toFloat()
                        }
                    )
                )
                c.drawRoundRect(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat(),
                    0F,
                    0F,
                    p
                )
                if (dX > 0) {
                    drawableComplete?.apply {
                        setTint(interpolateArgb(
                            iconDeactivatedColor,
                            applyAlpha(
                                if (dX > 0) primaryColor400 else vibrantColor400,
                                when {
                                    _opacity >= 1 -> 1F
                                    _opacity <= 0 -> 0F
                                    else -> (_opacity * 1).toFloat()
                                }
                            )
                        ))
                    }?.setBounds(
                        itemView.left + Dp24,
                        middleY - Dp24 / 2,
                        itemView.left + Dp24 + Dp24,
                        middleY + Dp24 / 2
                    )
                    drawableComplete?.draw(c)
                } else {
                    drawableDelete?.apply {
                        setTint(interpolateArgb(
                            iconDeactivatedColor,
                            applyAlpha(
                                if (dX > 0) primaryColor400 else vibrantColor400,
                                when {
                                    _opacity >= 1 -> 1F
                                    _opacity <= 0 -> 0F
                                    else -> (_opacity * 1).toFloat()
                                }
                            )
                        ))
                    }?.setBounds(
                        itemView.right - Dp24 - Dp24,
                        middleY - Dp24 / 2,
                        itemView.right - Dp24,
                        middleY + Dp24 / 2
                    )
                    drawableDelete?.draw(c)
                }

                itemView.elevation = 12 * (_dist * 4).coerceAtMost(1F)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }
    }

}