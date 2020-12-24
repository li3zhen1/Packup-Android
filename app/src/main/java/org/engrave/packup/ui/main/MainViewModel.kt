package org.engrave.packup.ui.main

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engrave.packup.data.deadline.DeadlineFilter
import org.engrave.packup.data.deadline.DeadlineSortOrder

class MainViewModel : ViewModel() {
    val deadlineSortOrder = MutableLiveData(DeadlineSortOrder.DUE_TIME_ASCENDING)
    val deadlineFilter = MutableLiveData(DeadlineFilter.PENDING_TO_COMPLETE)
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