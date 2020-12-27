package org.engrave.packup.ui.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.engrave.packup.R
import org.engrave.packup.ui.editdeadline.EditDeadlineViewModel
import org.engrave.packup.util.view.requestFocusAndShowSoftKeyboard
import org.engrave.packup.util.view.setOnTextChangedListener

class ModifyDescriptionBottomSheetFragment: BottomSheetDialogFragment() {
    private val vm: DeadlineDetailViewModel by activityViewModels()
    private lateinit var editText: EditText
    private lateinit var finishButton: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_edit_deadline_description, container, false)
        .apply {
            editText = findViewById(R.id.edit_desc_fragment_edit_text)
            finishButton = findViewById(R.id.edit_desc_fragment_button)
            finishButton.setOnClickListener {
                vm.setDeadlineDescription(editText.text.toString())
                dismiss()
            }
            editText.setText(vm.deadline.value?.description)
            editText.requestFocusAndShowSoftKeyboard(context)
        }

}