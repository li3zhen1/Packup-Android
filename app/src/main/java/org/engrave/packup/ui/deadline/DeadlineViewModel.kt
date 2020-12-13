package org.engrave.packup.ui.deadline

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineRepository
import org.engrave.packup.data.deadline.DeadlineSortOrder

class DeadlineViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /* TODO: 保存默认状态 */
    val sortOrder = MutableLiveData(DeadlineSortOrder.SOURCE_COURSE_NAME)
    val filterIsDeleted = MutableLiveData(false)
    val filterIsSubmitted = MutableLiveData<Boolean?>(null)
    val filterIsExpired = MutableLiveData<Boolean?>(null)
    val certainCourseName = MutableLiveData<String?>(null)

    private val deadlines: LiveData<List<Deadline>> = deadlineRepository.allDeadlines

    val sortedDeadlines = MediatorLiveData<List<DeadlineItem>?>()
        .apply {
            value = listOf()
            addSource(deadlines) {
                this.value = it
                    .applyLiveDateToCrossFilter()
                    .sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
            }
            addSource(sortOrder) {
                this.value = deadlines.value
                    ?.applyLiveDateToCrossFilter()
                    ?.sortAndGroup(it)
                    ?: listOf()
            }
            addSource(filterIsDeleted) {
                this.value = deadlines.value
                    ?.applyLiveDateToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }

            addSource(filterIsSubmitted) {
                this.value = deadlines.value
                    ?.applyLiveDateToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }

            addSource(filterIsExpired) {
                this.value = deadlines.value
                    ?.applyLiveDateToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }
            addSource(certainCourseName) {
                this.value = deadlines.value
                    ?.applyLiveDateToCrossFilter()
                    ?.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_ASCENDING)
                    ?: listOf()
            }
        }

    private fun List<Deadline>.applyLiveDateToCrossFilter() = this.crossFilter(
        filterIsDeleted.value ?: false,
        filterIsSubmitted.value,
        filterIsExpired.value,
        certainCourseName.value
    )

    fun setStarred(uid: Int, boolean: Boolean) {
        viewModelScope.launch {
            deadlineRepository.setStarred(uid, boolean)
        }
    }
}