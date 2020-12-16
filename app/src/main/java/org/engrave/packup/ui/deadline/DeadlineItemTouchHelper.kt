package org.engrave.packup.ui.deadline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.engrave.packup.R
import org.engrave.packup.util.inDp


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

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val drawableClose =
            ContextCompat.getDrawable(context, R.drawable.ic_fluent_comment_delete_24_regular)
        val d_finish =
            ContextCompat.getDrawable(context, R.drawable.ic_fluent_checkmark_circle_24_regular)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView: View = viewHolder.itemView
            val p = Paint()
            val bg = Paint()
            if (viewHolder is DeadlineListAdapter.DeadlineMemberViewHolder) {
                val middleY: Int = (itemView.top + itemView.bottom) / 2
                if (dX > 0) {
                    var opacity: Float = 0.1f + dX / (itemView.right - itemView.left)
                    opacity = if (opacity > 1) 1F else opacity
                    val nonOpacityColor = ContextCompat.getColor(context, R.color.color_primary_600)
                    bg.color = nonOpacityColor
                    p.color = nonOpacityColor and 0x00ffffff or
                            (opacity * 0xff).toInt() shl 24
                } else {
                    var opacity: Float = 0.1f + dX / (itemView.left - itemView.getRight())
                    opacity = if (opacity > 1) 1F else opacity
                    val nonOpacityColor = ContextCompat.getColor(context, R.color.color_vibrant_400)
                    bg.color = nonOpacityColor
                    p.color = nonOpacityColor and 0x00ffffff or
                            (opacity * 0xff).toInt() shl 24
                }
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
                    c.drawCircle(
                        (itemView.left + Dp24 * 1.5).toFloat(), middleY.toFloat(), dX / 2, bg
                    )
                    d_finish?.setBounds(
                        itemView.left + Dp24,
                        middleY - Dp24 / 2,
                        itemView.left + Dp24 + Dp24,
                        middleY + Dp24 / 2
                    )
                    d_finish?.draw(c)
                } else {
                    c.drawCircle(
                        (itemView.right - Dp24 * 1.5).toFloat(), middleY.toFloat(), -dX / 2, bg
                    )
                    drawableClose?.setBounds(
                        itemView.right - Dp24 - Dp24,
                        middleY - Dp24 / 2,
                        itemView.right - Dp24,
                        middleY + Dp24 / 2
                    )
                    drawableClose?.draw(c)
                }
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