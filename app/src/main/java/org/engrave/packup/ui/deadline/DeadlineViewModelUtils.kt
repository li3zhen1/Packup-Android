package org.engrave.packup.ui.deadline

import org.engrave.packup.data.deadline.*
import java.util.*

fun List<Deadline>.sortAndGroup(
    order: DeadlineSortOrder,
    baselineTime: Long = Calendar.getInstance().timeInMillis
): List<DeadlineItem> {
    val groups = when (order) {
        DeadlineSortOrder.DUE_TIME_ASCENDING -> this
            .sortedBy { it.due_time }
            .groupByDueTime()
            .mapKeys { it.key.toString() }
        DeadlineSortOrder.DUE_TIME_DESCENDING -> this
            .sortedByDescending { it.due_time }
            .groupByDueTime()
            .mapKeys { it.key.toString() }
        DeadlineSortOrder.ASSIGNED_TIME_ASCENDING -> this.sortedBy { it.crawl_update_time }
            .groupByAssignedTime()
            .mapKeys { it.key.toString() }
        DeadlineSortOrder.ASSIGNED_TIME_DESCENDING -> this
            .sortedByDescending { it.crawl_update_time }
            .groupByAssignedTime()
            .mapKeys { it.key.toString() }

        DeadlineSortOrder.SOURCE_COURSE_NAME -> this
            .sortedBy { it.source_name }
            .groupBy { it.source_name }
            .mapKeys { it.key ?: "其它" }
        DeadlineSortOrder.INFERRED_SUBJECT -> this
            .sortedBy { it.inferred_subject }
            .groupBy { it.inferred_subject }
            .mapKeys { it.key ?: "其它" }
        DeadlineSortOrder.IMPORTANCE_DESCENDING -> this
            .sortedByDescending { it.importance }
            .groupBy { it.importance }
            .mapKeys { it.key.toString() }
        DeadlineSortOrder.SUBMISSION -> this
            .sortedBy { it.has_submission }
            .groupBy { !it.has_submission }
            .mapKeys { if (it.key) "未提交" else "已提交" }
    }
    return groups.flatMap {
        listOf(
            DeadlineHeader(it.key, it.value.size)
        ) + it.value.map {
            DeadlineMember(it)
        }
    }
}