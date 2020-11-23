package org.engrave.packup.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engrave.packup.data.deadline.DeadlineSortOrder

class MainViewModel : ViewModel() {
    val deadlineSortOrder = MutableLiveData(DeadlineSortOrder.DUE_TIME_ASCENDING)

}