package org.engrave.packup.data.deadline

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.engrave.packup.api.pku.course.DeadlineRawJson
import org.engrave.packup.data.IContentComparable
import android.icu.text.SimpleDateFormat
import org.engrave.packup.util.*
import java.util.*

@Entity
data class Deadline(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    val name: String?,
    val description: String?,
    val event_type: String?,
    val course_object_id: String?,
    val source_name: String?,
    val due_time: Long?,
    val reminder: Long?,
    val is_completed: Boolean,
    val is_deleted: Boolean,
    val is_starred: Boolean,
    val has_submission: Boolean,

    /* 用户无关字段 */
    val crawl_update_time: Long?,   // 从教学网抓下来的时间
    val sync_time: Long?,           // 和服务器同步的时间
): IContentComparable<Deadline> {
    val importance: Int get() = 0
    val inferred_subject: String? get() = null

    val source_course_name: String get() = source_name?.substringBeforeLast("(") ?: ""

    /* 仅比较用户有关字段 */
    override fun isOfSameContent(other: Deadline) =
        uid == other.uid && name == other.name && description == other.description
                && event_type == other.event_type && course_object_id == other.course_object_id
                && source_name == other.source_name && reminder == other.reminder
                && due_time == other.due_time && is_completed == other.is_completed
                && is_deleted == other.is_deleted && is_starred == other.is_starred
                && has_submission == other.has_submission

    companion object {
        private val zuluFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);

        fun fromRawJson(it: DeadlineRawJson) = Deadline(
            // TODO: uid 换一种方式
            uid = it.itemSourceId.replace("_", "").toInt(),
            name = it.title,
            description = it.calendarId,
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
        )
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

enum class DeadlineSortFilter {
    NOT_STASHED,
    ONLY_STASHED,
    NOT_SUBMITTED,
    SUBMITTED,
}

enum class DueTimeNode {
    UNKNOWN,
    EXPIRED,
    DUE_WITHIN_1_HOUR,
    DUE_WITHIN_24_HOUR,
    DUE_WITHIN_72_HOUR,
    DUE_WITHIN_7_DAYS,
    DUE_WITHIN_30_DAYS,
    MORE_THAN_ONE_MONTH_LEFT
}

enum class AssignedTimeNode {
    UNKNOWN,
    RECENT_1_HOUR,
    RECENT_24_HOUR,
    RECENT_72_HOUR,
    RECENT_7_DAYS,
    RECENT_30_DAYS,
    LONG_LONG_AGO,
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

