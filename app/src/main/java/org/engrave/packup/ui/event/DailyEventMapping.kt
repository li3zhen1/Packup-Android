package org.engrave.packup.ui.event

import org.engrave.packup.data.course.ClassInfo
import org.engrave.packup.data.course.ClassWeekType
import org.engrave.packup.util.DAY_IN_MILLIS
import org.engrave.packup.util.DAY_IN_MILLIS_LONG
import org.engrave.packup.util.asCalendar
import org.engrave.packup.util.getWeekStart

fun List<ClassInfo>.transformToWeeklyAspect(nthWeek: Int): List<List<DailyCourseItem>> {
    val isEvenWeek = (nthWeek.rem(2) == 0)
    val dailyEvents = Array<MutableList<DailyCourseItem>>(7) {
        mutableListOf()
    }
    this.forEach {
        it.classTime.forEach { timePeriod ->
            if ((isEvenWeek && (timePeriod.weekType == ClassWeekType.EVEN))
                || ((!isEvenWeek) && (timePeriod.weekType == ClassWeekType.ODD))
                || timePeriod.weekType == ClassWeekType.EVERY
            ) {
                if (nthWeek in timePeriod.startWeekIndex..timePeriod.endWeekIndex)
                    dailyEvents[timePeriod.dayOfWeek - 1].add(
                        DailyCourseItem(
                            startMinute = 0,
                            endMinute = 0,
                            classInfoUid = it.uid ?: -1,
                            eventName = it.courseName,
                            place = timePeriod.classroom
                        )
                    )
            }
        }
    }
    return dailyEvents.map { it }
}

// 对应日子零点的时间
val semester2020Start = 1598889600000L
val semester2020End = 1610208000000L

fun List<ClassInfo>.collectSemesterEventItems(
    semesterStartInMillis: Long,
    semesterEndInMillis: Long
): List<DailyEventsItem> {
    val firstWeekMonday = semesterStartInMillis.asCalendar().getWeekStart()
    val lastWeekMonday = semesterEndInMillis.asCalendar().getWeekStart() + 7
    val startDaysSliced = ((semesterStartInMillis - firstWeekMonday) / DAY_IN_MILLIS).toInt()
    val endDaysSliced = (((lastWeekMonday - firstWeekMonday) / DAY_IN_MILLIS) + 7).toInt()
    return (1..((lastWeekMonday - firstWeekMonday) / DAY_IN_MILLIS_LONG).toInt()).flatMap { weekNum ->
        transformToWeeklyAspect(weekNum).mapIndexed { index, list ->
            DailyEventsItem(
                startOfDayInMillis = DAY_IN_MILLIS_LONG * index + firstWeekMonday,
                deadlines = listOf(),
                courses = list
            )
        }
    }.drop(startDaysSliced).dropLast(endDaysSliced)
}