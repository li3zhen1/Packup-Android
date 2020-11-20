package org.engrave.packup.data.deadline

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.engrave.packup.api.pku.course.DeadlineRawJson
import java.text.SimpleDateFormat
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
    val reminder: Long?,
    val update_time: Long,
    val due_time: Long?,
    val is_finished: Boolean,
    val is_deleted: Boolean,
    val is_starred: Boolean,
    val has_submission: Boolean
) {
    val importance: Int
        get() = 0

    val inferred_subject: String
        get() = ""

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
            update_time = Date().time,
            due_time = zuluFormatter.parse(it.endDate).time,
            is_finished = false,
            is_deleted = false,
            is_starred = false,
            has_submission = false
        )
    }
}

enum class DeadlineSortOrder {
    DUE_TIME_ASCENDING,
    DUE_TIME_DESCENDING,
    UPDATE_TIME_ASCENDING,
    UPDATE_TIME_DESCENDING,
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

fun List<Deadline>.filteredSortAndGroup(
    order: DeadlineSortOrder,
    filter: DeadlineSortFilter = DeadlineSortFilter.NOT_STASHED
) =
    when (order) {
        DeadlineSortOrder.DUE_TIME_ASCENDING -> this.sortedBy { it.due_time }
        DeadlineSortOrder.DUE_TIME_DESCENDING -> this.sortedByDescending { it.due_time }
        DeadlineSortOrder.UPDATE_TIME_ASCENDING -> this.sortedBy { it.update_time }
        DeadlineSortOrder.UPDATE_TIME_DESCENDING -> this.sortedByDescending { it.update_time }
        DeadlineSortOrder.SOURCE_COURSE_NAME -> this.sortedBy { it.source_name }
        DeadlineSortOrder.INFERRED_SUBJECT -> this.sortedBy { it.inferred_subject }
        DeadlineSortOrder.IMPORTANCE_DESCENDING -> this.sortedByDescending { it.importance }
        DeadlineSortOrder.SUBMISSION -> this.sortedBy { it.has_submission }
    }