package org.engrave.packup.ui.event

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.engrave.packup.R
import org.engrave.packup.util.inDp

class EventAdapter(
    private val context: Context
) : RecyclerView.Adapter<EventAdapter.DailyViewHolder>() {

    private var eventList: List<DailyEventsItem> = listOf()
    fun postList(ss: List<DailyEventsItem>) {
        eventList = ss
        notifyDataSetChanged()
    }

    inner class DailyViewHolder(private val itemView: View, private val parentHeight: Int) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var eventDateHeroText: TextView
        lateinit var eventContainer: FrameLayout

        var canvasBaseY: Int = 36.inDp(context)
        var canvasHeight: Int = parentHeight - canvasBaseY
        var minuteHeight = canvasHeight.toFloat() / (960) // 8:00 ~ 22:00 + 更早/ 更晚

        private fun Int.toY() = ((this - 420) * minuteHeight).toInt()

        fun bind(dailyRoutineItem: DailyEventsItem) {
            eventContainer = itemView.findViewById(R.id.event_item_day_container)
            eventDateHeroText = itemView.findViewById(R.id.event_date_title)
            eventDateHeroText.text = dailyRoutineItem.startOfDayInMillis.toString()
            dailyRoutineItem.courses.forEach {
                eventContainer.addView(
                    generateClassInfoGrid(it)
                )
            }
        }

        private fun generateClassInfoGrid(course: DailyCourseItem) = Button(context).apply {
            isAllCaps = false
            gravity = Gravity.START or Gravity.TOP
            text = SpannableStringBuilder(
                "${course.eventName}\n${course.place}"
            ).apply {
                setSpan(
                    AbsoluteSizeSpan(12, true),
                    course.eventName.length + 1,
                    course.eventName.length + course.place.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            y = (course.startMinute.toY()).toFloat()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ((course.endMinute - course.startMinute) * minuteHeight).toInt()
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_day, parent, false)
        view.layoutParams.width = parent.measuredWidth / 3
        return DailyViewHolder(view, parent.measuredHeight)
    }

    override fun onViewRecycled(holder: DailyViewHolder) {
        super.onViewRecycled(holder)
        holder.eventContainer.removeAllViews()
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size
}