package org.engrave.packup.ui.event

import org.engrave.packup.data.deadline.Deadline

data class DailyEventsItem(
    val startOfDayInMillis: Int,
    val deadlines: List<Deadline>,
    val courses: List<DailyCourseItem>
)

data class DailyCourseItem(
    val startMinute: Int,
    val endMinute: Int,
    val classInfoUid: Int,
    val eventName: String,
    val place: String,
)