package org.engrave.packup.ui.deadline

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineRepository
import org.engrave.packup.data.deadline.DeadlineSortOrder
import org.engrave.packup.data.deadline.getByUid

class DeadlineViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /* TODO: 保存默认状态 */
    val sortOrder = MutableLiveData(DeadlineSortOrder.SOURCE_COURSE_NAME)
    val filterShowDeleted = MutableLiveData(false)
    val filterShowCompleted = MutableLiveData(false)
    val filterIsSubmitted = MutableLiveData<Boolean?>(null)
    val filterIsExpired = MutableLiveData<Boolean?>(null)
    val certainCourseName = MutableLiveData<String?>(null)

    private val deadlines: LiveData<List<Deadline>> = deadlineRepository.allDeadlines

    val deadlineCrawlerRef = deadlineRepository.courseCrawler


    val sortedDeadlines = MediatorLiveData<List<DeadlineItem>?>()
        .apply {
            value = listOf()
            addSource(deadlines) {
                this.value = it
                    .applyLiveDataToCrossFilter()
                    .sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
            }
            addSource(sortOrder) {
                this.value = deadlines.value
                    ?.applyLiveDataToCrossFilter()
                    ?.sortAndGroup(it)
                    ?: listOf()
            }

            addSource(filterShowDeleted) {
                this.value = deadlines.value
                    ?.applyLiveDataToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }

            addSource(filterIsSubmitted) {
                this.value = deadlines.value
                    ?.applyLiveDataToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }

            addSource(filterIsExpired) {
                this.value = deadlines.value
                    ?.applyLiveDataToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }
            addSource(certainCourseName) {
                this.value = deadlines.value
                    ?.applyLiveDataToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }
        }

    fun setDeadlineStarred(uid: Int, boolean: Boolean) = viewModelScope.launch {
        deadlineRepository.setDeadlineStarred(uid, boolean)
    }

    fun setDeadlineDeleted(uid: Int, boolean: Boolean) = viewModelScope.launch {
        deadlineRepository.setDeadlineDeleted(uid, boolean)
    }

    fun setDeadlineCompleted(uid: Int, boolean: Boolean) = viewModelScope.launch {
        deadlineRepository.setDeadlineCompleted(uid, boolean)
    }

    private fun List<Deadline>.applyLiveDataToCrossFilter() = this.crossFilter(
        filterShowDeleted.value ?: false,
        filterShowCompleted.value ?: false,
        filterIsSubmitted.value,
        filterIsExpired.value,
        certainCourseName.value
    )

    fun setStarred(uid: Int, boolean: Boolean) {
        viewModelScope.launch {
            deadlineRepository.setDeadlineStarred(uid, boolean)
        }
    }

    fun getDeadlineByUid(uid: Int) = deadlines.value?.getByUid(uid)
}