package org.engrave.packup.ui.event

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.api.pku.portal.Semester
import org.engrave.packup.api.pku.portal.SemesterSeason
import org.engrave.packup.data.course.ClassInfoRepository
import org.engrave.packup.data.deadline.DeadlineRepository

class EventViewModel @ViewModelInject constructor(
    private val classInfoRepository: ClassInfoRepository,
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel() {
    val classInfoList = classInfoRepository.allClassInfo
    val eventList: LiveData<List<DailyEventsItem>> = Transformations.map(classInfoList) { clasInfoList ->
        clasInfoList
            .filter { it.semester == Semester(2020, SemesterSeason.AUTUMN) }
            .collectSemesterEventItems(semester2020Start, semester2020End)
    }

    val nthWeek: MutableLiveData<Int> = MutableLiveData(0)


    init {
        viewModelScope.launch {
            classInfoRepository.crawlAllClassInfo()
        }
    }
}