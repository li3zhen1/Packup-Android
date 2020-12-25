package org.engrave.packup.ui.deadline

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.data.deadline.*

class DeadlineViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /* TODO: 保存默认状态 */
    val sortOrder = MutableLiveData(DeadlineSortOrder.SOURCE_COURSE_NAME)
    val filter = MutableLiveData(DeadlineFilter.PENDING_TO_COMPLETE)

/*    val filterIsSubmitted = MutableLiveData<Boolean?>(null)
    val filterIsExpired = MutableLiveData<Boolean?>(null)
    val certainCourseName = MutableLiveData<String?>(null)*/

    private val deadlines: LiveData<List<Deadline>> = deadlineRepository.allDeadlines

    val deadlineCrawlerRef = deadlineRepository.courseCrawler


    val sortedDeadlines = MediatorLiveData<List<DeadlineItem>?>()
        .apply {
            value = listOf()
            addSource(deadlines) {
                val isOriginallyEmpty = value.isNullOrEmpty()
                val newValue = it
                    .applyDeadlineFilter(filter.value)
                    .sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                if (!(isOriginallyEmpty && newValue.isNullOrEmpty()))
                    this.value = newValue
            }
            addSource(sortOrder) {
                val isOriginallyEmpty = value.isNullOrEmpty()
                val newValue = deadlines.value
                    ?.applyDeadlineFilter(filter.value)
                    ?.sortAndGroup(it)
                    ?: listOf()
                if (!(isOriginallyEmpty && newValue.isNullOrEmpty()))
                    this.value = newValue
            }

            addSource(filter) {
                this.value = deadlines.value
                    ?.applyDeadlineFilter(it)
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

    private fun List<Deadline>.applyDeadlineFilter(ft: DeadlineFilter?) = this.filter {
        if (ft != null)
            when (ft) {
                DeadlineFilter.PENDING_TO_COMPLETE -> !it.is_completed && !it.is_deleted
                DeadlineFilter.COMPLETED -> it.is_completed && !it.is_deleted
                DeadlineFilter.DELETED -> it.is_deleted
                else -> !it.is_completed && !it.is_deleted
            }
        else true
    }

    fun setStarred(uid: Int, boolean: Boolean) {
        viewModelScope.launch {
            deadlineRepository.setDeadlineStarred(uid, boolean)
        }
    }

    fun getDeadlineByUid(uid: Int) = deadlines.value?.getByUid(uid)
}