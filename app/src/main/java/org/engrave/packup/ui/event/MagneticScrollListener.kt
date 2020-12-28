package org.engrave.packup.ui.event

import androidx.recyclerview.widget.RecyclerView

class MagneticScrollListener(val r: RecyclerView) : RecyclerView.OnScrollListener() {
    var runnable = Runnable {
        smoothScrollToPosition()
    }

    @Volatile
    private var isUserControl = false

    override fun onScrolled(r: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(r, dx, dy)
        if (r.scrollState == RecyclerView.SCROLL_STATE_SETTLING && !isUserControl) {
            if (dx in -3..3) {
                r.stopScroll()
            }
        }
    }

    private fun smoothScrollToPosition() {
        isUserControl = true
        val stickyInfoView = r.getChildAt(0)
        val bottom = stickyInfoView.right
        val height = stickyInfoView.measuredWidth
        if (bottom != height) {
            if (bottom >= (height / 2)) {
                r.smoothScrollBy(-(height - bottom), 0)
            } else {
                r.smoothScrollBy(bottom, 0)
            }
        }
    }

    override fun onScrollStateChanged(r: RecyclerView, newState: Int) {
        super.onScrollStateChanged(r, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
            if (!isUserControl)
                r.postDelayed(runnable, 120)
        if (r.scrollState != RecyclerView.SCROLL_STATE_SETTLING)
            isUserControl = false
    }
}