package org.engrave.packup.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engrave.packup.data.deadline.DeadlineFilter
import org.engrave.packup.data.deadline.DeadlineSortOrder

class MainViewModel : ViewModel() {
    val deadlineSortOrder = MutableLiveData(DeadlineSortOrder.DUE_TIME_ASCENDING)
    val deadlineFilter = MutableLiveData(DeadlineFilter.PENDING_TO_COMPLETE)
    val fragmentId = MutableLiveData(0)

    companion object {
        const val FRAGMENT_ID_DEADLINE = 0
        const val FRAGMENT_ID_EVENT = 1
        const val FRAGMENT_ID_DOCUMENT = 2
    }
}