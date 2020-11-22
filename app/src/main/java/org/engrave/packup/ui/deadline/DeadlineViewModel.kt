package org.engrave.packup.ui.deadline

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import org.engrave.packup.data.deadline.*

class DeadlineViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /* TODO: 保存默认状态 */
    val sortOrder = MutableLiveData(DeadlineSortOrder.SOURCE_COURSE_NAME)

    private val deadlines: LiveData<List<Deadline>> = deadlineRepository.allDeadlines

    val sortedDeadlines = MediatorLiveData<List<DeadlineItem>?>()
        .apply {
            value = listOf()
            addSource(deadlines) {
                this.value =
                    it.sortAndGroup(sortOrder.value ?: DeadlineSortOrder.DUE_TIME_DESCENDING)
            }
            addSource(sortOrder) {
                this.value = deadlines.value?.sortAndGroup(it)
            }
        }

}