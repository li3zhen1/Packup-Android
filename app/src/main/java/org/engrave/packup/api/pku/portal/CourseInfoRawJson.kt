package org.engrave.packup.api.pku.portal


import kotlinx.serialization.Serializable

/**
 * Raw JSON format from Portal
 */
@Serializable
data class CourseInfoRawJson(
    val course: List<Course>,
    val remark: String,
    val success: Boolean
)

@Serializable
data class Course(
    val timeNum: String,
    val fri: Weekday,
    val mon: Weekday,
    val sat: Weekday,
    val sun: Weekday,
    val thu: Weekday,
    val tue: Weekday,
    val wed: Weekday
) {
    val weekdays: List<Weekday>
        get() = listOf(mon, tue, wed, thu, fri, sat, sun)

    //TODO: Language preference?
    val timeIndex get()= when(timeNum){
        "第一节" -> 1
        "第二节" -> 2
        "第三节" -> 3
        "第四节" -> 4
        "第五节" -> 5
        "第六节" -> 6
        "第七节" -> 7
        "第八节" -> 8
        "第九节" -> 9
        "第十节" -> 10
        "第十一节" -> 11
        "第十二节" -> 12
        else -> throw Exception("Unexpected time num.")
    }
}

@Serializable
data class Weekday(
    val courseName: String,
    val parity: String,
    val sty: String
)