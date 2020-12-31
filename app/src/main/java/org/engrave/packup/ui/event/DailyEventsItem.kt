package org.engrave.packup.ui.event

import org.engrave.packup.data.deadline.Deadline

data class DailyEventsItem(
    val startOfDayInMillis: Long,
    val deadlines: List<Deadline>,
    var courses: List<DailyCourseItem>,
    val nthWeek: Int,
)

data class DailyCourseItem(
    val startMinute: Int,
    val endMinute: Int,
    val classInfoUid: Int,
    val eventName: String,
    val place: String,
    val itemType: Int = COURSE
){
    companion object{
        const val COURSE = 0
        const val DEADLINE = 1
        const val EXAM = 2
    }
}