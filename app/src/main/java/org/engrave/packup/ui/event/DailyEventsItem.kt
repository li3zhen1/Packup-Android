package org.engrave.packup.ui.event

import org.engrave.packup.data.deadline.Deadline

data class DailyEventsItem(
    val startOfDayInMillis: Long,
    val deadlines: List<Deadline>,
    val courses: List<DailyCourseItem>,
    val nthWeek: Int,
)

data class DailyCourseItem(
    val startMinute: Int,
    val endMinute: Int,
    val classInfoUid: Int,
    val eventName: String,
    val place: String,
)