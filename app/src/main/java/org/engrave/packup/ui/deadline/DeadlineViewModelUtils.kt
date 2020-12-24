package org.engrave.packup.ui.deadline

import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineSortOrder
import org.engrave.packup.data.deadline.groupByAssignedTime
import org.engrave.packup.data.deadline.groupByDueTime
import java.util.*


fun List<Deadline>.crossFilter(
    showDeleted: Boolean = false,
    showCompleted: Boolean = false,
    showSubmitted: Boolean? = false,
    showExpired: Boolean? = null,
    certainCourseName: String? = null,
    baselineTime: Long = Calendar.getInstance().timeInMillis
): List<Deadline> = this.filter { ddl ->
    (ddl.is_deleted == showDeleted) && (ddl.is_completed == showCompleted)
            && (showSubmitted?.let { ddl.has_submission == showSubmitted } ?: true)
            /**
             * isExpired == null   ==>   true
             * isExpired != null:
             *     due_time == null  ==> true
             *     due_time != null  ==> Is it expired?
             */
            && (showExpired?.let { ddl.due_time?.let { dt -> dt <= baselineTime } ?: true } ?: true)
            && (certainCourseName?.let { ddl.source_name == certainCourseName } ?: true)
}

fun List<Deadline>.sortAndGroup(
    order: DeadlineSortOrder,
    baselineTime: Long = Calendar.getInstance().timeInMillis
): List<DeadlineItem> {
    return when (order) {
        DeadlineSortOrder.DUE_TIME_ASCENDING -> groupByDueTime(baselineTime)
            .toSortedMap { o1, o2 ->
                o1.ordinal - o2.ordinal
            }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedBy { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.DUE_TIME_DESCENDING -> groupByDueTime(baselineTime)
            .toSortedMap { o1, o2 ->
                o2.ordinal - o1.ordinal
            }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedByDescending { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.ASSIGNED_TIME_ASCENDING -> groupByAssignedTime()
            .toSortedMap { o1, o2 ->
                o1.ordinal - o2.ordinal
            }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedBy { it.crawl_update_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.ASSIGNED_TIME_DESCENDING -> groupByAssignedTime()
            .toSortedMap { o1, o2 ->
                o2.ordinal - o1.ordinal
            }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedByDescending { it.crawl_update_time }.map {
                    DeadlineMember(it)
                }
            }

        DeadlineSortOrder.SOURCE_COURSE_NAME -> groupBy { it.source_name }
            .toSortedMap { o1, o2 ->
                if (o1 == null && o2 == null) 0
                else if (o1 == null) -1
                else if (o2 == null) 1
                else if (o1 == o2) 0
                else if (o1 > o2) 1
                else -1
            }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key ?: "其它", entry.value.size)
                ) + entry.value.sortedBy { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.INFERRED_SUBJECT -> groupBy { it.inferred_subject }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key ?: "其它", entry.value.size)
                ) + entry.value.sortedBy { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.IMPORTANCE_DESCENDING -> groupBy { it.importance }
            .toSortedMap { o1, o2 ->
                o2 - o1
            }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(if(entry.key == 1) "重要" else "其它", entry.value.size)
                ) + entry.value.sortedBy { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.SUBMISSION -> groupBy { it.has_submission }
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(if (entry.key) "已提交" else "未提交", entry.value.size)
                ) + entry.value.sortedBy { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
    }
}