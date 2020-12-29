package org.engrave.packup.ui.event

import org.engrave.packup.data.course.ClassInfo
import org.engrave.packup.data.course.ClassWeekType

suspend fun List<ClassInfo>.transformToWeeklyAspect(nthWeek: Int): List<List<DailyCourseItem>> {
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

suspend fun collectSemesterCourseItems(semesterStartInMillis: Long, semesterEndInMillis: Long){

}