package org.engrave.packup.ui.event

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.data.course.ClassInfoRepository
import org.engrave.packup.data.deadline.DeadlineRepository

class EventViewModel @ViewModelInject constructor(
    private val classInfoRepository: ClassInfoRepository,
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel() {
    val classInfoList = classInfoRepository.allClassInfo
    val eventList: LiveData<List<DailyEventsItem>> = Transformations.map(classInfoList) {
        it.collectSemesterEventItems(semester2020Start, semester2020End)
    }


    init {
        viewModelScope.launch {
            classInfoRepository.crawlAllClassInfo()
        }
    }
}