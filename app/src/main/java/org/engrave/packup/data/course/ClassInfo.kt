package org.engrave.packup.data.course

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.engrave.packup.data.StringListTypeConverter
import org.engrave.packup.api.pku.portal.CourseInfoRawJson
import org.engrave.packup.api.pku.portal.Semester
import org.engrave.packup.api.pku.portal.Weekday
import org.engrave.packup.util.cleanXmlNodes
import org.engrave.packup.util.substringBetween
import org.jsoup.Jsoup
import java.util.*


/**
 * Class information fetched from [network.fetchPortalCourseInfo()]
 */
@Entity
@TypeConverters(StringListTypeConverter::class, ClassTimeListTypeConverters::class)
data class ClassInfo(
    @PrimaryKey(autoGenerate = true)
    val uid: Int?,
    val courseName: String,
    val description: String,
    val examInfo: String,
    val teachers: List<String>,
    val classTime: List<ClassTime>,
    val semester: Semester,
    val updateTimestamp: Long
) {
    val examDate get() = examInfo.substring(0..8).toInt()
    val examTime get() = examInfo.subSequence(8..10)
    override fun equals(other: Any?): Boolean {
        if (other is ClassInfo)
            return courseName == other.courseName
                    && description == other.description
                    && examInfo == other.examInfo
                    && classTime == other.classTime
                    && semester == other.semester
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return (courseName + description + examInfo + classTime + semester).hashCode()
    }

    companion object {
        /**
         * Convert from Json fetched via portal.pku.edu.cn
         */
        fun fromCourseRawJson(rawJson: CourseInfoRawJson, semester: Semester) = rawJson.course
            .flatMap { course ->
                course.weekdays.flatMapIndexed { index, weekday ->
                    // TimeIndex, DayOfWeek Index, info
                    weekday.courseName
                        .split("<br>")
                        .chunked(3)
                        .map {
                            Triple(
                                course.timeIndex, index + 1, Weekday(
                                    courseName = it.joinToString("<br>"),
                                    parity = weekday.parity,
                                    sty = weekday.sty
                                )
                            )
                        }
                }
            }.filter {
                it.third.courseName.isNotBlank()
            }.groupBy {
                it.third.courseName.substringBefore("<br>")
            }.map { mapEntry ->
                val infoList = mapEntry.value[0].third.courseName.split("<br>")
                val teach = infoList[1]
                    .substringBetween("教师：", " ")
                    .split(",")
                    .sorted()
                val desc = infoList[1]
                    .substringAfter("备注：", "")
                    .replace("""[ ]+""".toRegex(), " ")
                    .trim()
                val exam = infoList[2]
                    .substringAfter("考试信息：", "")
                    .let {
                        Regex("""[0-9]{8}""").find(it)?.value.orEmpty() +
                                Regex("""[上下晚][午上]""").find(it)?.value.orEmpty()
                    }
                ClassInfo(
                    uid = null,
                    courseName = mapEntry.key.substringBeforeLast("(").cleanXmlNodes(),
                    description = desc,
                    teachers = teach,
                    examInfo = exam,
                    classTime = mapEntry.value.groupBy { it.second }.map {
                        val info = it.value[0].third.courseName.split("<br>")
                        val startWeek = info[1].substringBetween("上课信息：", "-").toInt()
                        val endWeek = info[1].substringBetween("-", "周 ").toInt()
                        val wType = when (info[1].substringBetween("周 ", "周")) {
                            "每" -> ClassWeekType.EVERY
                            "双" -> ClassWeekType.EVEN
                            else -> ClassWeekType.ODD
                        }
                        ClassTime(
                            nthClassStart = it.value.minOf { t -> t.first },
                            nthClassEnd = it.value.maxOf { t -> t.first },
                            dayOfWeek = it.value[0].second,
                            weekType = wType,
                            startWeekIndex = startWeek,
                            endWeekIndex = endWeek,
                            classroom = info[1].split(" ").run {
                                if (size > 2) get(2)
                                else ""
                            }
                        )
                    }.sortedBy { it.dayOfWeek },
                    semester = semester,
                    updateTimestamp = Date().time
                )
            }
            .sortedBy { it.courseName }

        /**
         * Use elective at the beginning of a semester.
         */
        fun fromElectiveResultHtml(rawHtmlString: String) =
            Jsoup.parse(rawHtmlString)
                .selectFirst("table .datagrid")
                .allElements
                .filter {
                    it.tagName() == "tr"
                }.filter {
                    it.childrenSize() == 11 && it.child(8).text() == "已选上"
                    // TODO: Language Preference 是英文是选课网可能是英文
                }.map { it ->
                    val info = Regex("""[0-9]{1,2}~[0-9]{1,2}周 [每双单]周周.[0-9]{1,2}~[0-9]{1,2}""")
                        .findAll(
                            it.child(7).text(),
                            0
                        )

                    ClassInfo(
                        uid = null,
                        courseName = it.child(0).text(),
                        examInfo = it.child(7).text()
                            .substringAfter("考试时间：", "")
                            .substringBefore("；", "")
                            .let {
                                Regex("""[0-9]{8}""").find(it)?.value.orEmpty() +
                                        Regex("""[上下晚][午上]""").find(it)?.value.orEmpty()
                            },
                        description = it.child(7).text()
                            .substringAfter("备注：", "")
                            .substringBeforeLast(")", "")
                            .replace("""[ ]+""".toRegex(), " ")
                            .trim(),
                        teachers = it.child(4).text()
                            .split(",")
                            .map { it.substringBefore('(') }
                            .run {
                                if (size >= 4)
                                    subList(0, 4)
                                else this
                            }
                            .sorted(),
                        classTime = info
                            .map { matchResult ->
                                val mv = matchResult.value
                                val classTimeRange = Regex("""[0-9]{1,2}~[0-9]{1,2}""")
                                    .findAll(mv, 0)
                                ClassTime(
                                    nthClassStart = classTimeRange.last().value.substringBefore('~')
                                        .toInt(),
                                    nthClassEnd = classTimeRange.last().value.substringAfter('~')
                                        .toInt(),
                                    dayOfWeek = when (mv.substringAfter("周周")[0]) {
                                        '一' -> 1
                                        '二' -> 2
                                        '三' -> 3
                                        '四' -> 4
                                        '五' -> 5
                                        '六' -> 6
                                        else -> 7
                                        //TODO: Parse Error?
                                    },
                                    weekType = when {
                                        mv.contains("每") -> ClassWeekType.EVERY
                                        mv.contains("单") -> ClassWeekType.ODD
                                        mv.contains("双") -> ClassWeekType.EVEN
                                        else -> ClassWeekType.EVEN
                                    },
                                    startWeekIndex = mv.substringBefore("~").toInt(),
                                    endWeekIndex = mv.substringBetween("~", "周").toInt(),
                                    classroom = it.child(7).text()
                                        .replace("""\(备注.*\)""".toRegex(), "")
                                        .split(" ")
                                        .elementAtOrNull(2)
                                        .run {
                                            when {
                                                isNullOrBlank() -> ""
                                                startsWith("考试") -> ""
                                                contains("~") -> ""
                                                else -> this
                                            }
                                        }
                                )
                            }
                            .toList()
                            .sortedBy { it.dayOfWeek },
                        semester = Semester.fromCurrentTime(),
                        updateTimestamp = Date().time
                    )
                }.sortedBy { it.courseName }
    }
}

/**
 * Class information fetched from [network.elective.fetchElectiveResultTable()]
 * Use this when Portal is unavailable.
 * (Approximately the first 2 weeks of a semester)
 */
data class ElectiveClassInfo(
    val courseName: String,
    val courseCategory: String,
    val courseCredit: Int,
    val hoursPerWeek: Int,
    val teachers: List<String>,
    val classId: Int,
    val department: String,
    val detailedInfo: String,
    val classTime: List<ClassTime>
)

enum class ClassWeekType(val value: Int) {
    EVERY(0),
    ODD(1),
    EVEN(2)
}

data class ClassTime(
    val nthClassStart: Int,
    val nthClassEnd: Int,
    /**
     * 1:星期一，2: 星期二，... 7：星期日
     */
    val dayOfWeek: Int,
    val weekType: ClassWeekType,
    val startWeekIndex: Int,
    val endWeekIndex: Int,
    val classroom: String
)

fun List<ClassTime>.serializedString() = this.joinToString("\n") {
    "${it.nthClassStart} ${it.nthClassEnd} ${it.dayOfWeek} ${it.weekType.value} ${it.startWeekIndex} ${it.endWeekIndex} ${it.classroom}"
}

class ClassTimeListTypeConverters {
    @TypeConverter
    fun classTimeListToString(classTimes: List<ClassTime>) = classTimes.serializedString()

    @TypeConverter
    fun stringToClassTimeList(str: String): List<ClassTime> = str.split("\n")
        .map { entry ->
            entry.split(" ").let {
                ClassTime(
                    nthClassStart = it[0].toInt(),
                    nthClassEnd = it[1].toInt(),
                    dayOfWeek = it[2].toInt(),
                    weekType = when (it[3].toInt()) {
                        0 -> ClassWeekType.EVERY
                        1 -> ClassWeekType.ODD
                        else -> ClassWeekType.EVEN
                    },
                    endWeekIndex = it[4].toInt(),
                    startWeekIndex = it[5].toInt(),
                    classroom = it[6]
                )
            }
        }

    @TypeConverter
    fun stringToSemester(str: String) = Semester.fromCode(str)

    @TypeConverter
    fun semesterToString(smt: Semester) = smt.asCode()
}