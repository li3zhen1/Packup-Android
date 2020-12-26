package org.engrave.packup.ui.editdeadline

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineRepository
import java.util.*

class EditDeadlineViewModel @ViewModelInject constructor(
    private val deadlineRepository: DeadlineRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val editingDeadlineTitle = MutableLiveData("")
    val editingDeadlineDescription = MutableLiveData("")
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DATE, 3)
    }
    val dueYear = MutableLiveData(calendar.get(Calendar.YEAR))

    // [0, 11]
    val dueMonth = MutableLiveData(calendar.get(Calendar.MONTH))
    val dueDate = MutableLiveData(calendar.get(Calendar.DAY_OF_MONTH))
    val dueHour = MutableLiveData(23)
    val dueMinutes = MutableLiveData(30)

    val _dueTimeStamp
        get() = Date(
            (dueYear.value ?: 2020) - 1900,
            dueMonth.value ?: 11,
            dueDate.value ?: 25,
            dueHour.value ?: 23,
            dueMinutes.value ?: 30
        ).time

    val dueTimeStamp = MediatorLiveData<Long>().apply {
        value = _dueTimeStamp
        addSource(dueYear) {
            value = _dueTimeStamp
        }
        addSource(dueMonth) {
            value = _dueTimeStamp
        }
        addSource(dueDate) {
            value = _dueTimeStamp
        }
        addSource(dueHour) {
            value = _dueTimeStamp
        }
        addSource(dueMinutes) {
            value = _dueTimeStamp
        }
    }

    fun commitDeadline() {
        viewModelScope.launch {
            deadlineRepository.commitNewDeadline(
                Deadline(
                    uid = 0,
                    name = editingDeadlineTitle.value.orEmpty(),
                    description = editingDeadlineDescription.value.orEmpty(),
                    calendar_id = null,
                    event_type = null,
                    course_object_id = null,
                    source_name = null,
                    due_time = dueTimeStamp.value,
                    reminder = null,
                    is_completed = false,
                    is_starred = false,
                    is_deleted = false,
                    has_submission = false,
                    crawl_update_time = null,
                    sync_time = null,
                    attached_file_list = listOf(),
                    attached_file_list_crawled = false
                )
            )
        }
    }
}