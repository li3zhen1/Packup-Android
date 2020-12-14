package org.engrave.packup.data.deadline

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.engrave.packup.api.pku.course.DeadlineRawJson
import org.engrave.packup.component.images.FILE_TYPE_ICON_MAP
import org.engrave.packup.data.IPayloadChangeAnimatable
import org.engrave.packup.util.*
import java.util.*

@Entity
@TypeConverters(DeadlineAttachedFileTypeConverter::class)
data class Deadline(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    val name: String?,
    var description: String?,
    val calendar_id: String?,
    val event_type: String?,
    val course_object_id: String?,
    val source_name: String?,
    val due_time: Long?,
    val reminder: Long?,
    val is_completed: Boolean,
    val is_deleted: Boolean,
    val is_starred: Boolean,
    var has_submission: Boolean,

    /* 用户无关字段 */
    val crawl_update_time: Long?,   // 从教学网抓下来的时间
    val sync_time: Long?,           // 和服务器同步的时间

    /* 更新标记 */
    val update_field_flag: Int = 0,
    val update_field_time: Long = 0,

    /* 本地缓存 */
    var attached_file_list: List<DeadlineAttachedFile>,
    var attached_file_list_crawled: Boolean = false

) : IPayloadChangeAnimatable<Deadline> {
    val importance: Int get() = 0
    val inferred_subject: String? get() = null

    val source_course_name_without_semester: String
        get() = source_name?.substringBeforeLast("(") ?: ""

    override fun keyFieldsSameWith(other: Deadline) =
        uid == other.uid && name == other.name && calendar_id == other.calendar_id
                && event_type == other.event_type && course_object_id == other.course_object_id
                && source_name == other.source_name && reminder == other.reminder
                && due_time == other.due_time && is_completed == other.is_completed
                && is_deleted == other.is_deleted

    override fun manipulatableFieldsSameWith(other: Deadline): Boolean =
        is_starred == other.is_starred && has_submission == other.has_submission


    companion object {
        fun fromRawJson(it: DeadlineRawJson) = Deadline(
            uid = it.itemSourceId.replace("_", "").toInt(),
            name = it.title,
            calendar_id = it.calendarId,
            event_type = it.eventType,
            course_object_id = it.itemSourceId,
            source_name = it.calendarName,
            reminder = null,
            due_time = fromZuluFormat(it.endDate).timeInMillis,
            is_completed = false,
            is_deleted = false,
            is_starred = false,
            has_submission = false,
            crawl_update_time = Date().time,
            sync_time = null,
            attached_file_list = listOf(),
            description = null //To be crawled
        )
    }


}

data class DeadlineAttachedFile(
    val fileName: String,
    val url: String,
    val downloadStatus: Int,
    val path: String
) {
    val fileType
        get() =
            fileName.substringAfterLast(".").run {
                if (isNullOrBlank()) "genericfile"
                else FILE_TYPE_ICON_MAP.getOrDefault(this, "genericfile")
            }
}

class DeadlineAttachedFileTypeConverter {

    @TypeConverter
    fun fileListToString(strings: List<DeadlineAttachedFile>) =
        strings.joinToString(CONVERT_DELIMITER) {
            "${it.fileName}\n${it.url}\n${it.downloadStatus}\n${it.path}"
        }

    @TypeConverter
    fun stringToFileList(string: String) =
        if (string == "") listOf()
        else string.split(CONVERT_DELIMITER)
            .chunked(4) {
                DeadlineAttachedFile(
                    it[0],
                    it[1],
                    it[2].toIntOrNull() ?: -1,
                    it[3]
                )
            }

    companion object {
        const val CONVERT_DELIMITER = "\n"
    }
}

enum class DeadlineSortOrder {
    DUE_TIME_ASCENDING,
    DUE_TIME_DESCENDING,
    ASSIGNED_TIME_ASCENDING,
    ASSIGNED_TIME_DESCENDING,
    SOURCE_COURSE_NAME,
    INFERRED_SUBJECT,
    IMPORTANCE_DESCENDING,
    SUBMISSION
}



enum class DueTimeNode{
    UNKNOWN,
    EXPIRED,
    DUE_WITHIN_1_HOUR,
    DUE_WITHIN_24_HOUR,
    DUE_WITHIN_72_HOUR,
    DUE_WITHIN_7_DAYS,
    DUE_WITHIN_30_DAYS,
    MORE_THAN_ONE_MONTH_LEFT
}

enum class AssignedTimeNode{
    UNKNOWN,
    RECENT_1_HOUR,
    RECENT_24_HOUR,
    RECENT_72_HOUR,
    RECENT_7_DAYS,
    RECENT_30_DAYS,
    LONG_LONG_AGO
}


fun List<Deadline>.groupByDueTime(baselineTime: Long = Calendar.getInstance().timeInMillis) =
    this.groupBy {
        if (it.due_time == null || it.due_time == 0L) DueTimeNode.UNKNOWN
        else (it.due_time - baselineTime).run {
            when {
                this <= 0 -> DueTimeNode.EXPIRED
                this < HOUR_IN_MILLIS -> DueTimeNode.DUE_WITHIN_1_HOUR
                this < DAY_IN_MILLIS -> DueTimeNode.DUE_WITHIN_24_HOUR
                this < 3 * DAY_IN_MILLIS -> DueTimeNode.DUE_WITHIN_72_HOUR
                this < WEEK_IN_MILLIS -> DueTimeNode.DUE_WITHIN_7_DAYS
                this < MONTH_IN_MILLIS -> DueTimeNode.DUE_WITHIN_30_DAYS
                else -> DueTimeNode.MORE_THAN_ONE_MONTH_LEFT
            }
        }
    }

fun List<Deadline>.groupByAssignedTime(baselineTime: Long = Calendar.getInstance().timeInMillis) =
    this.groupBy {
        if (it.crawl_update_time == null || it.crawl_update_time == 0L) DueTimeNode.UNKNOWN
        else (baselineTime - it.crawl_update_time).run {
            when {
                this <= 0 -> AssignedTimeNode.UNKNOWN
                this < HOUR_IN_MILLIS -> AssignedTimeNode.RECENT_1_HOUR
                this < DAY_IN_MILLIS -> AssignedTimeNode.RECENT_24_HOUR
                this < 3 * DAY_IN_MILLIS -> AssignedTimeNode.RECENT_72_HOUR
                this < WEEK_IN_MILLIS -> AssignedTimeNode.RECENT_7_DAYS
                this < MONTH_IN_MILLIS -> AssignedTimeNode.RECENT_30_DAYS
                else -> AssignedTimeNode.LONG_LONG_AGO
            }
        }
    }


fun List<Deadline>.getByUid(uid:Int) = find { it.uid == uid }
