package org.engrave.packup.apitest

import org.engrave.packup.api.pku.course.fetchCourseLoginCookies
import org.engrave.packup.api.pku.course.fetchDeadlineDetailHtml
import org.engrave.packup.pw
import org.engrave.packup.sid
import org.engrave.packup.util.asDocument
import org.engrave.packup.util.attachCookie
import org.engrave.packup.util.scanAsString
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL

class DeadlineDetailCrawlTest {
    @Test
    fun getDetailHtml() {
        val courseLoggedCookie = fetchCourseLoginCookies(
            sid,
            pw
        )
        val doc = fetchDeadlineDetailHtml(
            "_150158_1",
            courseLoggedCookie
        )
        println(doc)

    }
}