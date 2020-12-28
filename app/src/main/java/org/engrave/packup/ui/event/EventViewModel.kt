package org.engrave.packup.ui.event

import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.engrave.packup.data.course.ClassInfoRepository
import org.engrave.packup.data.deadline.DeadlineRepository

class EventViewModel @ViewModelInject constructor(
    private val classInfoRepository: ClassInfoRepository,
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel() {
    val classInfoList = classInfoRepository.allClassInfo

    init {
        viewModelScope.launch {
            classInfoRepository.crawlAllClassInfo()
        }
    }
}