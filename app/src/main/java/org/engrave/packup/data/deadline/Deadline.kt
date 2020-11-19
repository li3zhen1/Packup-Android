package org.engrave.packup.data.deadline

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.engrave.packup.api.pku.course.DeadlineRawJson
import java.text.SimpleDateFormat
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
    val is_starred: Boolean
){
    companion object{
        private val zuluFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);

        fun fromRawJson(it: DeadlineRawJson): Deadline{
            return Deadline(
                // TODO: uid 换一种方式
                uid = it.itemSourceId.replace("_","").toInt(),
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
                is_starred = false
            )
        }
    }
}