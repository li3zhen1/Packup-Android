package org.engrave.packup.ui.main

import androidx.core.content.ContextCompat
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engrave.packup.data.deadline.DeadlineFilter
import org.engrave.packup.data.deadline.DeadlineSortOrder
import org.engrave.packup.util.getMonth
import java.util.*

class MainViewModel : ViewModel() {
    val deadlineSortOrder = MutableLiveData(DeadlineSortOrder.DUE_TIME_ASCENDING)
    val deadlineFilter = MutableLiveData(DeadlineFilter.PENDING_TO_COMPLETE)

    val eventViewTimeStamp = MutableLiveData(Calendar.getInstance().timeInMillis)

    val fragmentId = MutableLiveData(0)

    val statusBarStatus = MediatorLiveData<Int>().apply {
        addSource(fragmentId) {
            value =
                when (it) {
                    FRAGMENT_ID_DEADLINE -> when (deadlineFilter.value) {
                        DeadlineFilter.DELETED -> STATUS_BAR_DEADLINE_DELETED
                        DeadlineFilter.COMPLETED -> STATUS_BAR_DEADLINE_COMPLETED
                        else -> STATUS_BAR_DEADLINE_NORMAL
                    }
                    FRAGMENT_ID_DOCUMENT -> STATUS_BAR_DOCUMENT
                    else -> STATUS_BAR_EVENT
                }
        }
        addSource(deadlineFilter) {
            value =
                when (fragmentId.value) {
                    FRAGMENT_ID_DEADLINE -> when (it) {
                        DeadlineFilter.DELETED -> STATUS_BAR_DEADLINE_DELETED
                        DeadlineFilter.COMPLETED -> STATUS_BAR_DEADLINE_COMPLETED
                        else -> STATUS_BAR_DEADLINE_NORMAL
                    }
                    FRAGMENT_ID_DOCUMENT -> STATUS_BAR_DOCUMENT
                    else -> STATUS_BAR_EVENT
                }
        }
    }

    val statusBarDisplayString = MediatorLiveData<String>().apply {
        addSource(eventViewTimeStamp) {
            value = getDisplayName(fragmentId.value, deadlineFilter.value, it)
        }
        addSource(fragmentId) {
            value = getDisplayName(it, deadlineFilter.value, eventViewTimeStamp.value)
        }
        addSource(deadlineFilter) {
            value = getDisplayName(fragmentId.value, it, eventViewTimeStamp.value)
        }
    }

    private fun getDisplayName(fragId: Int?, ddlFilter: DeadlineFilter?, time: Long?): String {
        return when (fragId) {
            FRAGMENT_ID_DEADLINE -> when (ddlFilter) {
                DeadlineFilter.DELETED -> "已删除的 Deadline"
                DeadlineFilter.COMPLETED -> "已完成的 Deadline"
                else -> "Deadline"
            }
            FRAGMENT_ID_DOCUMENT -> "文件"
            FRAGMENT_ID_EVENT -> "${
                Calendar.getInstance().apply {
                    timeInMillis = time ?: 0
                }.getMonth() + 1
            } 月"
            else -> "Packup"
        }
    }

    companion object {
        const val FRAGMENT_ID_DEADLINE = 0
        const val FRAGMENT_ID_EVENT = 1
        const val FRAGMENT_ID_DOCUMENT = 2
        const val STATUS_BAR_DEADLINE_NORMAL = 0
        const val STATUS_BAR_DEADLINE_DELETED = 1
        const val STATUS_BAR_DEADLINE_COMPLETED = 2
        const val STATUS_BAR_EVENT = 3
        const val STATUS_BAR_DOCUMENT = 4
    }
}