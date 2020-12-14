package org.engrave.packup.ui.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineRepository
import org.engrave.packup.data.deadline.getByUid

class DeadlineDetailViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val deadlineUid: MutableLiveData<Int> = MutableLiveData()
    val deadlines = deadlineRepository.allDeadlines

    fun setDeadlineUid(uid: Int) {
        deadlineUid.value = uid
    }

    val deadline = MediatorLiveData<Deadline?>().apply {
        addSource(deadlineUid) {
            this.value = deadlines.value?.getByUid(it)
        }
        addSource(deadlines) {
            this.value = deadlineUid.value?.let { it1 -> it.getByUid(it1) }
        }
    }

    fun alterStarredAsync() = viewModelScope.launch {
        deadlineUid.value?.let {
            deadline.value?.let { deadlineVal ->
                deadlineRepository.setStarred(
                    it,
                    !deadlineVal.is_starred
                )
            }
        }
    }
}