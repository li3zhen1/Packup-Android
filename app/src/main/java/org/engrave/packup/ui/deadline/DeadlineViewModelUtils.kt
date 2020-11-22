package org.engrave.packup.ui.deadline

import android.util.Log
import org.engrave.packup.data.deadline.*
import java.util.*

fun List<Deadline>.sortAndGroup(
    order: DeadlineSortOrder,
    baselineTime: Long = Calendar.getInstance().timeInMillis
): List<DeadlineItem> {
    val date = Date().time
    Log.e("", "start at $date")
    val groups = when (order) {
        DeadlineSortOrder.DUE_TIME_ASCENDING -> groupByDueTime()
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedBy { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.DUE_TIME_DESCENDING -> groupByDueTime()
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedByDescending { it.due_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.ASSIGNED_TIME_ASCENDING -> groupByAssignedTime()
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedBy { it.crawl_update_time }.map {
                    DeadlineMember(it)
                }
            }
        DeadlineSortOrder.ASSIGNED_TIME_DESCENDING -> groupByAssignedTime()
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
                ) + entry.value.sortedByDescending { it.crawl_update_time }.map {
                    DeadlineMember(it)
                }
            }

        DeadlineSortOrder.SOURCE_COURSE_NAME -> groupBy { it.source_name }
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
            .flatMap { entry ->
                listOf(
                    DeadlineHeader(entry.key.toString(), entry.value.size)
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

    Log.e("", "end after ${Date().time - date}")
    return groups
}