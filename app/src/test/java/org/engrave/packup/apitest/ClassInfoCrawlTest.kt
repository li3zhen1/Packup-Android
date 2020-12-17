package org.engrave.packup.apitest

import kotlinx.coroutines.runBlocking
import org.engrave.packup.api.pku.portal.Semester
import org.engrave.packup.api.pku.portal.SemesterSeason
import org.engrave.packup.api.pku.portal.fetchPortalCourseInfo
import org.engrave.packup.api.pku.portal.fetchPortalLoginCookies
import org.engrave.packup.data.course.ClassInfo
import org.engrave.packup.pw
import org.engrave.packup.sid
import org.junit.Test

class ClassInfoCrawlTest {
    @Test
    fun crawlTest() {
        val semester = Semester(
            2020,
            SemesterSeason.AUTUMN
        )
        val rawJson = fetchPortalCourseInfo(
            semester,
            runBlocking { fetchPortalLoginCookies(sid, pw) }
        )
        ClassInfo.fromCourseRawJson(rawJson, semester).apply(::println)
    }
}