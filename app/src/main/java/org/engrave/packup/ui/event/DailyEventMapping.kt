@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package org.engrave.packup.ui.event

import android.util.Log
import org.engrave.packup.data.course.ClassInfo
import org.engrave.packup.data.course.ClassWeekType
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.util.*
import java.text.SimpleDateFormat
import java.util.*

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
    return dailyEvents.map { it }
}

fun Long.GmtToChn() = this + 8 * HOUR_IN_MILLIS

// 对应日子零点的时间
val semester2020Start = 1600617600000L.GmtToChn()
val semester2020End = 1611331200000L.GmtToChn()

fun List<ClassInfo>.collectSemesterEventItems(
    semesterStartInMillis: Long,
    semesterEndInMillis: Long
): List<DailyEventsItem> {
    val firstWeekMonday = semesterStartInMillis.asGmtCalendar().getWeekStart()
    val lastWeekMonday = semesterEndInMillis.asGmtCalendar().getWeekStart()

    val startDaysSliced = ((semesterStartInMillis - firstWeekMonday) / DAY_IN_MILLIS).toInt()
    val endDaysSliced = (7 - ((semesterEndInMillis - lastWeekMonday) / DAY_IN_MILLIS)).toInt()
    val routines =
        (1..((lastWeekMonday - firstWeekMonday) / WEEK_IN_MILLIS_LONG).toInt() + 1).flatMap { weekNum ->
            transformToWeeklyAspect(weekNum).mapIndexed { index, list ->
                DailyEventsItem(
                    startOfDayInMillis = DAY_IN_MILLIS_LONG * index + firstWeekMonday + (weekNum - 1) * WEEK_IN_MILLIS_LONG,
                    deadlines = listOf(),
                    courses = list,
                    nthWeek = weekNum
                )
            }
        }.drop(startDaysSliced)
    forEach {
        if ((it.examInfo.isNotBlank()) && it.uid != null) {
            getStartOfDayInMillisWith(it.examInfo)?.let { ts ->
                val tgt = ts.GmtToChn()
                routines.find { itm ->
                    itm.startOfDayInMillis == tgt
                }?.run {
                    courses = courses + DailyCourseItem(
                        startMinute = when (it.examInfo[8]) {
                            '上' -> 510
                            '下' -> 870
                            else -> 1110
                        },
                        endMinute = when (it.examInfo[8]) {
                            '上' -> 630
                            '下' -> 990
                            else -> 1230
                        },
                        classInfoUid = it.uid,
                        eventName = "期末考试",
                        place = it.courseName,
                        itemType = DailyCourseItem.EXAM
                    )
                }
            }
        }
    }
    return routines
}

fun List<DailyEventsItem>.collectDeadlines(deadlines: List<Deadline>): List<DailyEventsItem> {
    deadlines.forEach {
        if (it.due_time != null) {
            var routineIndex = -1
            var routine: DailyEventsItem? = null
            for (i in indices) {
                if (this[i].startOfDayInMillis > it.due_time) {
                    routineIndex = i - 1
                    break;
                }
            }
            if(routineIndex>0) {
                this[routineIndex].courses = this[routineIndex].courses + DailyCourseItem(
                    startMinute = Calendar.getInstance().apply {
                        timeInMillis = it.due_time
                    }.let { cld ->
                        cld.add(Calendar.HOUR, 12)
                        cld.get(Calendar.MINUTE) + cld.get(Calendar.HOUR) * 60
                    },
                    endMinute = 0,
                    classInfoUid = it.uid,
                    eventName = it.name,
                    place = "",
                    itemType = DailyCourseItem.DEADLINE
                )
            }
        }
    }
    return this
}


fun getStartOfDayInMillisWith(examInfo: String): Long? {
    return try {
        val date = examInfo.slice(0 until 8)
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        val time = fmt.parse(date)
        time.time
        // 格林威治时间
    } catch (e: Exception) {
        null
    }
}