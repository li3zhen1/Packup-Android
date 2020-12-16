package org.engrave.packup.ui.attached

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engrave.packup.data.deadline.DeadlineAttachedFile


class AttachedFileBottomSheetViewModel @ViewModelInject constructor(

) : ViewModel() {
    val fileBlob = MutableLiveData<DeadlineAttachedFile>()

}