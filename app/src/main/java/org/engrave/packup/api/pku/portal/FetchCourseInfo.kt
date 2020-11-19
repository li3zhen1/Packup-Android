package org.engrave.packup.api.pku.portal

import kotlinx.serialization.decodeFromString
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.JsonSerializer
import org.engrave.packup.util.makeURL
import org.engrave.packup.util.scanAsString
import java.net.HttpURLConnection
import java.util.*


private const val CourseInfoBaseUrl =
    "https://portal.pku.edu.cn/portal2017/bizcenter/course/getCourseInfo.do"

fun fetchPortalCourseInfo(semesterCode: String, loggedCookie: DummyCookie): CourseInfoRawJson {
    val conn = makeURL(
        CourseInfoBaseUrl,
        mapOf("xndxq" to semesterCode)
    ).openConnection() as HttpURLConnection
    val jsonResponse = conn.run {
        setRequestProperty("Cookie", loggedCookie.toString())
        inputStream.scanAsString()
    }
    return JsonSerializer.decodeFromString<CourseInfoRawJson>(jsonResponse)
        .also { conn.disconnect() }
}

fun fetchPortalCourseInfo(semester: Semester, loggedCookie: DummyCookie) =  fetchPortalCourseInfo(
    semester.asCode(),
    loggedCookie
)

enum class SemesterSeason(val value: Int) {
    AUTUMN(1),
    SPRING(2),
    SUMMER(3)
}

data class Semester(
    val yearStart: Int,
    val season: SemesterSeason
) {
    fun asCode() = "${yearStart % 100}-${(yearStart + 1) % 100}-${season.value}"

    companion object {
        fun fromCurrentTime(): Semester{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            return when(calendar.get(Calendar.MONTH)){
                // TODO: !!! from campus calendar & elective status
                in 1..6 -> Semester(
                    year-1,
                    SemesterSeason.SPRING
                )
                in 6..8 -> Semester(
                    year -1,
                    SemesterSeason.SUMMER
                )
                else -> Semester(
                    year,
                    SemesterSeason.AUTUMN
                )
            }
        }

        fun fromCode(code: String) = code.split("-").let {
            if (it.size < 3) throw Exception("Invalid semester code.")
            Semester(
                2000 + it[0].toInt(),
                when (it[2]) {
                    "1" -> SemesterSeason.AUTUMN
                    "2" -> SemesterSeason.SPRING
                    else -> SemesterSeason.SUMMER
                }
            )
        }
    }
}
