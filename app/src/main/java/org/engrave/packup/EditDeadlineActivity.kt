package org.engrave.packup

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.ActivityEditDeadlineBinding
import org.engrave.packup.ui.editdeadline.EditDeadlineViewModel
import org.engrave.packup.ui.editdeadline.EditDescriptionBottomSheetFragment
import org.engrave.packup.ui.editdeadline.EditDueDateTimeBottomSheetFragment
import org.engrave.packup.util.toGlobalizedString
import org.engrave.packup.util.view.requestFocusAndShowSoftKeyboard
import org.engrave.packup.util.view.setOnTextChangedListener
import java.util.*


@AndroidEntryPoint
class EditDeadlineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditDeadlineBinding

    private val editDeadlineViewModel: EditDeadlineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDeadlineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addDeadlineTitle.setOnTextChangedListener { s, _, _, _ ->
            editDeadlineViewModel.editingDeadlineTitle.value = s.toString()
        }
        binding.deadlineDetailNavButton.setOnClickListener {
            finish()
        }
        binding.deadlineDetailDescButton.setOnClickListener {
            EditDescriptionBottomSheetFragment().show(
                supportFragmentManager,
                "EDIT_DESCRIPTION"
            )
        }
        binding.deadlineDetailDueButton.setOnClickListener {
            EditDueDateTimeBottomSheetFragment().show(
                supportFragmentManager,
                "DUE_TIME_SETTER"
            )
        }

        editDeadlineViewModel.editingDeadlineDescription.observe(this) {
            binding.deadlineDetailDescButton.apply {
                text = if (it.isNullOrBlank()) "添加备注" else it
                setTextColor(ContextCompat.getColor(
                    context,
                    if (it.isNullOrBlank()) R.color.colorText else R.color.colorHeroText
                ))
            }
        }
        editDeadlineViewModel.apply {
            dueTimeStamp.observe(this@EditDeadlineActivity) {
                binding.deadlineDetailDueButton.apply {
                    text = Calendar.getInstance().apply {
                        time = Date(it)
                    }.toGlobalizedString(
                        this@EditDeadlineActivity,
                        autoOmitYear = false,
                        omitTime = false,
                        omitWeek = false
                    )
                }
            }
        }


        binding.editDeadlineFragmentConfirmButton.setOnClickListener {
            if (editDeadlineViewModel.editingDeadlineTitle.value.isNullOrBlank()) {
                binding.addDeadlineTitle.requestFocusAndShowSoftKeyboard(this)
                return@setOnClickListener
            }
            editDeadlineViewModel.commitDeadline()
            finish()
        }


        binding.addDeadlineTitle.requestFocusAndShowSoftKeyboard(this)
    }
}