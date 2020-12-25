package org.engrave.packup.ui.editdeadline

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        add(Calendar.DATE, 2)
    }
    var dueYear = calendar.get(Calendar.YEAR)
    var dueMonth = calendar.get(Calendar.MONTH)
    var dueDate = calendar.get(Calendar.DAY_OF_MONTH)
    var dueHour = 23
    var dueMinutes = 30

    val dueTimeStamp
        get() = Date(
            dueYear,
            dueMonth,
            dueDate,
            dueHour,
            dueMinutes
        ).time

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
                    due_time = null,
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