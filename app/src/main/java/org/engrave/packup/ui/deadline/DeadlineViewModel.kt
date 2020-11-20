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
    val sortOrder = MutableLiveData(DeadlineSortOrder.DUE_TIME_DESCENDING)
    val sortFilter = MutableLiveData(DeadlineSortFilter.NOT_STASHED)

    private val deadlines: LiveData<List<Deadline>> = deadlineRepository.allDeadlines

    val sortedDeadlines = MediatorLiveData<List<Deadline>?>()
        .apply {
            value = listOf()
            addSource(deadlines) {
                this.value = it.filteredSortAndGroup(
                    sortOrder.value ?: DeadlineSortOrder.DUE_TIME_DESCENDING,
                    sortFilter.value ?: DeadlineSortFilter.NOT_STASHED
                )
            }
            addSource(sortFilter) {
                this.value = deadlines.value?.filteredSortAndGroup(
                    sortOrder.value ?: DeadlineSortOrder.DUE_TIME_DESCENDING,
                    it
                )
            }
            addSource(sortOrder) {
                this.value = deadlines.value?.filteredSortAndGroup(
                    it,
                    sortFilter.value ?: DeadlineSortFilter.NOT_STASHED
                )
            }
        }
}