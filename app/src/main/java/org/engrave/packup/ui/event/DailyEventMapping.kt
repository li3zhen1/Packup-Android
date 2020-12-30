package org.engrave.packup.ui.event

import android.util.Log
import org.engrave.packup.data.course.ClassInfo
import org.engrave.packup.data.course.ClassWeekType
import org.engrave.packup.util.*

fun Int._asStartTime() = when (this) {
    1 -> 480
    2 -> 540
    3 -> 610
    4 -> 670
    5 -> 780
    6 -> 840
    7 -> 910
    8 -> 970
    9 -> 1120
    10 -> 1180
    else -> 1240
}

fun Int.asStartTime() = when (this) {
    1 -> 480
    2 -> 540
    3 -> 610
    4 -> 670
    5 -> 780
    6 -> 840
    7 -> 910
    8 -> 970
    9 -> 1030
    10 -> 1120
    11 -> 1180
    else -> 1240
}

fun Int.asEndTime() = this.asStartTime() + 50

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
                if (nthWeek in timePeriod.endWeekIndex..timePeriod.startWeekIndex)
                    dailyEvents[timePeriod.dayOfWeek - 1].add(
                        DailyCourseItem(
                            startMinute = timePeriod.nthClassStart.asStartTime(),
                            endMinute = timePeriod.nthClassEnd.asEndTime(),
                            classInfoUid = it.uid ?: -1,
                            eventName = it.courseName,
                            place = timePeriod.classroom
                        )
                    )
            }
        }
    }
//    Log.e("DAILY", dailyEvents.joinToString("\n") { it.toString() })

    return dailyEvents.map { it }
}

// 对应日子零点的时间
val semester2020Start = 1600617600000L + 8 * HOUR_IN_MILLIS
val semester2020End = 1609948800000L + 8 * HOUR_IN_MILLIS

fun List<ClassInfo>.collectSemesterEventItems(
    semesterStartInMillis: Long,
    semesterEndInMillis: Long
): List<DailyEventsItem> {
    val firstWeekMonday = semesterStartInMillis.asGmtCalendar().getWeekStart()
    Log.e("FIRST", firstWeekMonday.toString())
    val lastWeekMonday = semesterEndInMillis.asGmtCalendar().getWeekStart()
    Log.e("LAST", lastWeekMonday.toString())

    val startDaysSliced = ((semesterStartInMillis - firstWeekMonday) / DAY_IN_MILLIS).toInt()
    val endDaysSliced = (((semesterEndInMillis - lastWeekMonday) / DAY_IN_MILLIS) + 7).toInt()
    return (1..((lastWeekMonday - firstWeekMonday) / DAY_IN_MILLIS_LONG).toInt()).flatMap { weekNum ->
//        Log.e("TIME", (firstWeekMonday + (weekNum - 1) * WEEK_IN_MILLIS_LONG).toString())
        transformToWeeklyAspect(weekNum).mapIndexed { index, list ->
            DailyEventsItem(
                startOfDayInMillis = DAY_IN_MILLIS_LONG * index + firstWeekMonday + (weekNum - 1) * WEEK_IN_MILLIS_LONG,
                deadlines = listOf(),
                courses = list,
                nthWeek = weekNum
            )
        }
    }.drop(startDaysSliced).dropLast(endDaysSliced)
//        .apply {
//        forEach {
//            it.courses.forEach {
//                Log.e("as","${it.startMinute}-${it.endMinute}")
//            }
//        }
//    }
}