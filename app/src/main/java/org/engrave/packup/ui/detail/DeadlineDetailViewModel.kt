package org.engrave.packup.ui.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineRepository

class DeadlineDetailViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val deadline: MutableLiveData<Deadline> = MutableLiveData()
    fun setDeadlineUid(uid: Int) {
        viewModelScope.launch {
            deadline.value = deadlineRepository.getDeadline(uid)
        }
    }
}