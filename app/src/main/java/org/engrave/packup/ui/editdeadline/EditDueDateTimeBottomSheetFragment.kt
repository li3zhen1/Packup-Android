package org.engrave.packup.ui.editdeadline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.engrave.packup.R
import org.engrave.packup.databinding.FragmentEditDeadlineDateSetterBinding
import org.engrave.packup.util.*
import org.engrave.packup.util.view.requestFocusAndShowSoftKeyboard
import org.engrave.packup.util.view.setJumpToOnValidate
import org.engrave.packup.util.view.setOnTextChangedListener
import java.util.*

class EditDueDateTimeBottomSheetFragment() : BottomSheetDialogFragment() {

    private var _binding: FragmentEditDeadlineDateSetterBinding? = null
    private val binding get() = _binding!!
    private val editViewModel: EditDeadlineViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDeadlineDateSetterBinding.inflate(inflater, container, false)


        editViewModel.dueTimeStamp.observe(viewLifecycleOwner) {
            val now = Calendar.getInstance()
            binding.deadlineTimePickerDesc.apply {
                val leftTime = it - now.time.time
                text =
                    "现在是 ${now.toGlobalizedString(context, omitTime = true, omitWeek = false)}。\n" +
                            if (leftTime > 0) "新的 Deadline ${
                                when {
                                    leftTime > DAY_IN_MILLIS -> "将被设置为 ${leftTime.div(DAY_IN_MILLIS)} 天后到期。"
                                    leftTime > HOUR_IN_MILLIS -> "将被设置为 ${
                                        leftTime.div(
                                            HOUR_IN_MILLIS
                                        )
                                    } 小时后到期。"
                                    leftTime > MINUTE_IN_MILLIS -> "将被设置为 ${
                                        leftTime.div(
                                            MINUTE_IN_MILLIS
                                        )
                                    } 分钟后到期。"
                                    else -> "留给你的时间不多了。"
                                }
                            }" else "目前设置的时间已经过去。"
            }
        }

        binding.apply {
            editDescFragmentTimeYear.setText(
                String.format(
                    "%04d",
                    editViewModel.dueYear.value ?: 2020
                )
            )
            editDescFragmentTimeMonth.setText(
                String.format(
                    "%02d",
                    (editViewModel.dueMonth.value ?: 11) + 1
                )
            )
            editDescFragmentTimeDate.setText(
                String.format(
                    "%02d",
                    editViewModel.dueDate.value ?: 25
                )
            )
            editDescFragmentTimeHour.setText(
                String.format(
                    "%02d",
                    editViewModel.dueHour.value ?: 23
                )
            )
            editDescFragmentTimeMinute.setText(
                String.format(
                    "%02d",
                    editViewModel.dueMinutes.value ?: 30
                )
            )
            editDescFragmentTimeYear.setJumpToOnValidate(4, editDescFragmentTimeMonth,
                validator = { s, _, _, _ ->
                    val parsedYear = (s.toString().toIntOrNull() ?: -1)
                    if (parsedYear > 1898) {
                        editViewModel.dueYear.value = parsedYear
                        true
                    } else false
                }
            )
            editDescFragmentTimeMonth.setJumpToOnValidate(
                2,
                editDescFragmentTimeDate
            ) { s, _, _, _ ->
                val parsedMonth = (s.toString().toIntOrNull() ?: -1)
                if (parsedMonth in 1..12) {
                    editViewModel.dueMonth.value = parsedMonth - 1
                    true
                } else false
            }

            editDescFragmentTimeDate.setJumpToOnValidate(
                2,
                editDescFragmentTimeHour
            ) { s, _, _, _ ->
                val parsedDate = (s.toString().toIntOrNull() ?: -1)
                if (parsedDate in 1..((editViewModel.dueMonth.value ?: 11).daysInThisMonth(
                        editViewModel.dueYear.value ?: 2020
                    ))
                ) {
                    editViewModel.dueDate.value = parsedDate
                    true
                } else false
            }
            editDescFragmentTimeHour.setJumpToOnValidate(
                2,
                editDescFragmentTimeMinute
            ) { s, _, _, _ ->
                val parsedHour = (s.toString().toIntOrNull() ?: -1)
                if (parsedHour in 0..23) {
                    editViewModel.dueHour.value = parsedHour
                    true
                } else false
            }
            editDescFragmentTimeMinute.setOnTextChangedListener { s, start, before, count ->
                val parsedM = (s.toString().toIntOrNull() ?: -1)
                val valid = parsedM in 0..59
                if (valid) editViewModel.dueMinutes.value = parsedM
                else editDescFragmentTimeMinute.selectAll()
            }

            editDescFragmentTimeDate.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeDate.apply {
                        setText(
                            String.format(
                                "%02d",
                                editViewModel.dueDate.value ?: 25
                            )
                        )
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.primaryTextFieldBackground
                        )
                    }
            }
            editDescFragmentTimeYear.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeYear.apply {
                        setText(
                            String.format(
                                "%04d",
                                editViewModel.dueYear.value ?: 2020
                            )
                        )
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.primaryTextFieldBackground
                        )
                    }
            }
            editDescFragmentTimeMonth.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeMonth.apply {
                        setText(
                            String.format(
                                "%02d",
                                (editViewModel.dueMonth.value ?: 11) + 1
                            )
                        )
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.primaryTextFieldBackground
                        )
                    }
            }
            editDescFragmentTimeHour.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeHour.apply {
                        setText(
                            String.format(
                                "%02d",
                                editViewModel.dueHour.value ?: 23
                            )
                        )
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.primaryTextFieldBackground
                        )
                    }
            }
            editDescFragmentTimeMinute.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeMinute.apply {
                        setText(
                            String.format(
                                "%02d",
                                editViewModel.dueMinutes.value ?: 30
                            )
                        )
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.primaryTextFieldBackground
                        )
                    }
            }
            editCompleteButton.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editDescFragmentTimeMonth.requestFocusAndShowSoftKeyboard(
            binding.root.context,
            true
        )
        binding.editDescFragmentTimeMonth.selectAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}