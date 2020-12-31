package org.engrave.packup.ui.event

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.DrawableMarginSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.engrave.packup.R
import org.engrave.packup.component.indicator.DeadlineIndicator
import org.engrave.packup.util.asCalendar
import org.engrave.packup.util.getDate
import org.engrave.packup.util.getDayOfWeek
import org.engrave.packup.util.inDp
import ws.vinta.pangu.Pangu


class EventAdapter(
    private val context: Context
) : RecyclerView.Adapter<EventAdapter.DailyViewHolder>() {

    private var eventList: List<DailyEventsItem> = listOf()
    fun postList(list: List<DailyEventsItem>) {
        eventList = list
        notifyDataSetChanged()

    }



    class DailyViewHolder(private val itemView: View, private val parentHeight: Int) :
        RecyclerView.ViewHolder(itemView) {

        companion object{
            private val pangu = Pangu()
        }
        lateinit var routine: DailyEventsItem
        private lateinit var eventDateHeroText: TextView
        lateinit var eventContainer: FrameLayout

        private var canvasBaseY: Int = 36.inDp(itemView.context)
        private val canvasHeight: Int get() = parentHeight - canvasBaseY
        private var minuteHeight = canvasHeight.toFloat() / (960) // 8:00 ~ 22:00 + 更早/ 更晚

        private fun Int.toY() = ((this - 420) * canvasHeight.toFloat() / 960).toInt()

        fun bind(dailyRoutineItem: DailyEventsItem) {
            routine = dailyRoutineItem

            eventContainer = itemView.findViewById(R.id.event_item_day_container)
            eventContainer.removeAllViews()
            eventDateHeroText = itemView.findViewById(R.id.event_date_title)
            eventDateHeroText.text = dailyRoutineItem.startOfDayInMillis.run {
                val cld = asCalendar()
                "${cld.getDate()} ${
                    when (cld.getDayOfWeek()) {
                        1 -> "周日"
                        2 -> "周一"
                        3 -> "周二"
                        4 -> "周三"
                        5 -> "周四"
                        6 -> "周五"
                        else -> "周六"
                    }
                }"
            }

            dailyRoutineItem.courses.forEach {
                when(it.itemType){
                    DailyCourseItem.COURSE -> eventContainer.addView(
                        generateClassInfoGrid(it)
                    )
                    DailyCourseItem.EXAM -> eventContainer.addView(
                        generateExamInfoGrid(it)
                    )
                    else -> eventContainer.addView(
                        generateDeadlineIndicator(it)
                    )
                }
            }
        }

        private fun generateClassInfoGrid(course: DailyCourseItem) = AppCompatButton(itemView.context).apply {
            isAllCaps = false
            gravity = Gravity.START or Gravity.TOP
            background = ContextCompat.getDrawable(context, R.drawable.course_button_default)
            elevation = 0F
            stateListAnimator = null
            setPadding(6.inDp(context), 2.inDp(context), 6.inDp(context), 2.inDp(context))

            val displayCourseName = course.eventName.replace("（", "(")
                .replace("）", ")")

            val displayPlaceName = pangu.spacingText(course.place)

            text = SpannableStringBuilder(
                "${displayCourseName}\n${displayPlaceName}"
            ).apply {
                setSpan(
                    AbsoluteSizeSpan(14, true),
                    0,
                    displayCourseName.length + displayPlaceName.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    displayCourseName.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.eventTextColor
                        )
                    ), 0, displayCourseName.length + displayPlaceName.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                if (displayPlaceName.isNotEmpty()) {
                    val drawable = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_loc
                    )
                    drawable?.let {
                        setSpan(
                            DrawableMarginSpan(it.apply {
                                setTint(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.eventTextColor
                                    )
                                )
                            }, 4),
                            displayCourseName.length + 1,
                            displayCourseName.length + 2,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
            y = (course.startMinute.toY()).toFloat()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ((course.endMinute - course.startMinute) * minuteHeight).toInt()
            )
        }

        private fun generateExamInfoGrid(course: DailyCourseItem) = AppCompatButton(itemView.context).apply {
            isAllCaps = false
            gravity = Gravity.START or Gravity.TOP
            background = ContextCompat.getDrawable(context, R.drawable.course_button_exam)
            elevation = 0F
            stateListAnimator = null
            setPadding(6.inDp(context), 2.inDp(context), 6.inDp(context), 2.inDp(context))

            val displayPlaceName = pangu.spacingText(course.place.replace("（", "(")
                .replace("）", ")"))

            text = SpannableStringBuilder(
                "${course.eventName}\n${displayPlaceName}"
            ).apply {
                setSpan(
                    AbsoluteSizeSpan(14, true),
                    0,
                    course.eventName.length + displayPlaceName.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    course.eventName.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.examTextColor
                        )
                    ), 0, course.eventName.length + displayPlaceName.length + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            y = (course.startMinute.toY()).toFloat()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ((course.endMinute - course.startMinute) * minuteHeight).toInt()
            )
        }

        private fun generateDeadlineIndicator(course: DailyCourseItem) = DeadlineIndicator(itemView.context).apply{
            displayText = course.eventName
            y = minuteHeight * 930 - 12.inDp(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_day, parent, false)
        view.layoutParams.width = parent.measuredWidth / 3
        return DailyViewHolder(view, parent.height)
    }

    override fun onViewRecycled(holder: DailyViewHolder) {
        super.onViewRecycled(holder)
        holder.eventContainer.removeAllViews()
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    override fun getItemId(position: Int): Long {
        return eventList[position].startOfDayInMillis
    }


}