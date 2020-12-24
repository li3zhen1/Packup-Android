package org.engrave.packup.data.deadline

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import androidx.core.content.FileProvider
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.engrave.packup.BuildConfig
import org.engrave.packup.api.pku.course.DeadlineRawJson
import org.engrave.packup.api.pku.course.downloadDeadlineAttachedFiles
import org.engrave.packup.component.images.FILE_TYPE_ICON_MAP
import org.engrave.packup.data.DEADLINE_ATTACHED_FILES_FOLDER
import org.engrave.packup.data.FILE_PROVIDER_AUTHORITY
import org.engrave.packup.data.IPayloadChangeAnimatable
import org.engrave.packup.util.*
import java.io.File
import java.util.*


@Entity
@TypeConverters(DeadlineAttachedFileTypeConverter::class)
data class Deadline(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    val name: String,
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
    val importance: Int get() = if (is_starred) 1 else 0
    val inferred_subject: String? get() = null

    val source_course_name_without_semester: String
        get() = source_name?.substringBeforeLast("(") ?: ""

    override fun keyFieldsSameWith(other: Deadline) =
        uid == other.uid && name == other.name && calendar_id == other.calendar_id
                && event_type == other.event_type && course_object_id == other.course_object_id
                && source_name == other.source_name && due_time == other.due_time

    override fun manipulatableFieldsSameWith(other: Deadline): Boolean =
        is_starred == other.is_starred && has_submission == other.has_submission


    suspend fun downloadAttachedFiles(context: Context, courseLoggedCookieString: String) {
        if (attached_file_list_crawled) {
            val rootAttachedFileFolder by lazy {
                File(
                    context.filesDir,
                    DEADLINE_ATTACHED_FILES_FOLDER
                ).also { if (!it.exists()) it.mkdir() }
            }
            val deadlineAttachedFileFolder by lazy {
                File(
                    rootAttachedFileFolder,
                    "uid$uid"
                ).also { if (!it.exists()) it.mkdir() }
            }
            attached_file_list.forEach {
                it.download(
                    courseLoggedCookieString,
                    deadlineAttachedFileFolder,
                    context
                )
            }
        }
    }

    fun applyUserSetFields(localDeadline: Deadline) = this.copy(
        uid = localDeadline.uid,
        reminder = localDeadline.reminder,
        is_completed = localDeadline.is_completed,
        is_deleted = localDeadline.is_deleted,
        is_starred = localDeadline.is_starred,
        has_submission = localDeadline.has_submission,

        crawl_update_time = localDeadline.crawl_update_time
    )


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
    var downloadStatus: Int,
    var localUri: String
) {
    val fileType
        get() =
            fileExt.run {
                if (isNullOrBlank()) "genericfile"
                else FILE_TYPE_ICON_MAP.getOrDefault(this, "genericfile")
            }
    val fileExt
        get() =
            fileName.substringAfterLast(".").toLowerCase()

    suspend fun download(
        courseLoggedCookieString: String,
        deadlineAttachedFileFolder: File,
        context: Context
    ) {
        val attachedFile = File(deadlineAttachedFileFolder, fileName)
        downloadStatus = STATUS_DOWNLOADING
        try {
            downloadDeadlineAttachedFiles(
                url,
                courseLoggedCookieString,
                attachedFile
            )
            localUri = FileProvider.getUriForFile(
                context,
                FILE_PROVIDER_AUTHORITY,
                attachedFile
            ).toString()
        } catch (e: Exception) {
            downloadStatus = STATUS_ERROR
            throw e
        }
        downloadStatus = STATUS_DOWNLOADED
    }

    val needAttemptDownload get() = downloadStatus <= 0

    fun startExternalOpenIntent(context: Context) {
        val uri = Uri.parse(localUri)
        context.grantUriPermission(
            BuildConfig.APPLICATION_ID,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            setDataAndType(uri, context.contentResolver.getType(uri))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun startShareIntent(context: Context) {
        val uri = Uri.parse(localUri)
        context.grantUriPermission(
            BuildConfig.APPLICATION_ID,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        context.startActivity(Intent.createChooser(intent, "分享 $fileName"))
    }

    fun asString() = "${fileName}\n${url}\n${downloadStatus}\n${localUri}"

    companion object {

        const val STATUS_ERROR = -1
        const val STATUS_NOT_DOWNLOADED = 0
        const val STATUS_DOWNLOADING = 1
        const val STATUS_DOWNLOADED = 2
        private const val PARCEL_DELIMITER = "\n"

        fun fromString(str: String): DeadlineAttachedFile? {
            Log.e("SPLIT", str)
            val sl = str.split(PARCEL_DELIMITER)
            return fromStringList(sl)
        }

        fun fromStringList(sl: List<String>) =
            if (sl.size == 4) DeadlineAttachedFile(
                sl[0],
                sl[1],
                sl[2].toIntOrNull() ?: -1,
                sl[3]
            ) else null
    }
}

class DeadlineAttachedFileTypeConverter {

    @TypeConverter
    fun fileListToString(strings: List<DeadlineAttachedFile>) =
        strings.joinToString(CONVERT_DELIMITER) {
            it.asString()
        }

    @TypeConverter
    fun stringToFileList(string: String) =
        if (string == "") listOf()
        else string.split(CONVERT_DELIMITER)
            .chunked(4) {
                //Log.e("convert", it.toString())
                DeadlineAttachedFile.fromStringList(it)
            }.filterNotNull()

    companion object {
        const val CONVERT_DELIMITER = "\n"
    }
}

enum class DeadlineSortOrder {
    DUE_TIME_ASCENDING {
        override fun toString() = "按截止时间升序"
    },
    DUE_TIME_DESCENDING{
        override fun toString() = "按截止时间降序"
    },
    ASSIGNED_TIME_ASCENDING{
        override fun toString() = "按布置时间升序"
    },
    ASSIGNED_TIME_DESCENDING{
        override fun toString() = "按布置时间降序"
    },
    SOURCE_COURSE_NAME{
        override fun toString() = "按课程名称排序"
    },
    INFERRED_SUBJECT{
        override fun toString() = "按学科排序"
    },
    IMPORTANCE_DESCENDING{
        override fun toString() = "按重要性排序"
    },
    SUBMISSION{
        override fun toString() = "按提交状态排序"
    }
}

enum class DeadlineFilter {
    PENDING_TO_COMPLETE {
        override fun toString() = "待提交"
    },
    COMPLETED{
        override fun toString() = "已完成"
    },
    DELETED{
        override fun toString() = "已删除"
    },
    SUBMITTED{
        override fun toString() = "已有提交"
    }
}


enum class DueTimeNode{
    UNKNOWN{
        override fun toString() = "未知"
    },
    EXPIRED{
        override fun toString() = "已逾期"
    },
    DUE_WITHIN_1_HOUR{
        override fun toString() = "1 小时内"
    },
    DUE_WITHIN_24_HOUR{
        override fun toString() = "24 小时内"
    },
    DUE_WITHIN_72_HOUR{
        override fun toString() = "3 天内"
    },
    DUE_WITHIN_7_DAYS{
        override fun toString() = "1 星期内"
    },
    DUE_WITHIN_30_DAYS{
        override fun toString() = "1 个月内"
    },
    MORE_THAN_ONE_MONTH_LEFT{
        override fun toString() = "多于 1 个月"
    }
}

enum class AssignedTimeNode{
    UNKNOWN{
        override fun toString() = "未知"
    },
    RECENT_1_HOUR{
        override fun toString() = "最近 1 小时"
    },
    RECENT_24_HOUR{
        override fun toString() = "最近 24 小时"
    },
    RECENT_72_HOUR{
        override fun toString() = "最近 3 天"
    },
    RECENT_7_DAYS{
        override fun toString() = "最近 7 天"
    },
    RECENT_30_DAYS{
        override fun toString() = "最近 1 个月"
    },
    LONG_LONG_AGO{
        override fun toString() = "更久远"
    }
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


fun List<Deadline>.getByUid(uid: Int) = find { it.uid == uid }
