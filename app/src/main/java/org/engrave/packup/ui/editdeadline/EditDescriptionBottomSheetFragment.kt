package org.engrave.packup.ui.editdeadline

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.engrave.packup.R
import org.engrave.packup.util.SimpleCountDown
import org.engrave.packup.util.view.requestFocusAndShowSoftKeyboard
import org.engrave.packup.util.view.setOnTextChangedListener


class EditDescriptionBottomSheetFragment: BottomSheetDialogFragment() {
    private val editDeadlineViewModel: EditDeadlineViewModel by activityViewModels()
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
                dismiss()
            }
            editText.setOnTextChangedListener { s, _, _, _ ->
                editDeadlineViewModel.editingDeadlineDescription.value = s.toString()
            }
            editText.setText(editDeadlineViewModel.editingDeadlineDescription.value)
            editText.requestFocusAndShowSoftKeyboard(context)
        }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}