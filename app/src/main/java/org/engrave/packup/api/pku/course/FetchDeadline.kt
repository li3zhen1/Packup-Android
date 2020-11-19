package org.engrave.packup.api.pku.course

import kotlinx.serialization.decodeFromString
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.JsonSerializer
import org.engrave.packup.util.makeURL
import org.engrave.packup.util.scanAsString
import java.net.HttpURLConnection
import java.util.*

private const val MILLISECONDS_OF_A_WEEK: Long = 604800000
private const val PkuCourseCalendarUrl =
    "https://course.pku.edu.cn/webapps/calendar/calendarData/selectedCalendarEvents"

fun fetchCourseDeadlines(
    cookie: DummyCookie,
    weeksBefore: Long = -4, weeksAfter: Long = 4
): List<DeadlineRawJson> {
    val currTimeInMilli: Long = Date().time
    val conn = makeURL(
        PkuCourseCalendarUrl,
        mapOf(
            "start" to (currTimeInMilli + MILLISECONDS_OF_A_WEEK * weeksBefore).toString(),
            "end" to (currTimeInMilli + MILLISECONDS_OF_A_WEEK * weeksAfter).toString(),
            "course_id" to "",
            "mode" to "personal"
        )
    ).openConnection() as HttpURLConnection
    conn.apply {
        requestMethod = "GET"
        instanceFollowRedirects = false
        setRequestProperty("Cookie", cookie.toString())
    }
    val connResponse = conn.inputStream.scanAsString()
    val deadlineRawJsonList = JsonSerializer.decodeFromString<List<DeadlineRawJson>>(connResponse)
    return deadlineRawJsonList.also { conn.disconnect() }
}
