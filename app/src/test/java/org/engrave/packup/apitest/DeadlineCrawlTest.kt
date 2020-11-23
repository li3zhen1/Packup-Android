package org.engrave.packup.apitest

import org.engrave.packup.api.pku.course.fetchCourseDeadlines
import org.engrave.packup.api.pku.course.fetchCourseLoginCookies
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.pw
import org.engrave.packup.sid
import org.engrave.packup.util.applyFormat
import org.engrave.packup.util.asGmtCalendar
import org.junit.Test
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DeadlineCrawlTest {
    @Test
    fun mainTest() {
        val loggedCookie = fetchCourseLoginCookies(
            sid, pw
        )

        for (i in 1..3) {
            val newDealines = fetchCourseDeadlines(
                loggedCookie
            ).map(Deadline::fromRawJson)
            for (newDealine in newDealines) {
                println(newDealine.due_time.asGmtCalendar().applyFormat())
            }
        }
    }

    @Test
    fun zuluFormatTest() {
        val formatted = "2020-10-31T15:30:00.000Z"
        val dt = Calendar.getInstance(TimeZone.getTimeZone("GMT")).apply {
            set(
                formatted.slice(0..3).toInt(),
                formatted.slice(5..6).toInt() - 1,
                formatted.slice(8..9).toInt(),
                formatted.slice(11..12).toInt(),
                formatted.slice(14..15).toInt(),
                formatted.slice(17..18).toInt()
            )
            set(
                Calendar.MILLISECOND,
                formatted.slice(20..22).toInt()
            )
        }
        println(
            dt.applyFormat()
        )
    }

    @Test
    fun stringFormatTest(){
        println(
            String.format("%s21353678%s", "qsa",2)
        )
    }

}