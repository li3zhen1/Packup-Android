package org.engrave.packup.ui.editdeadline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.engrave.packup.databinding.FragmentEditDeadlineDateSetterBinding
import org.engrave.packup.util.daysInThisMonth
import org.engrave.packup.util.view.requestFocusAndShowSoftKeyboard
import org.engrave.packup.util.view.setJumpToOnValidate
import org.engrave.packup.util.view.setOnTextChangedListener

class EditDateTimeBottomSheetFragment() : BottomSheetDialogFragment() {

    private var _binding: FragmentEditDeadlineDateSetterBinding? = null
    private val binding get() = _binding!!
    private val editViewModel: EditDeadlineViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDeadlineDateSetterBinding.inflate(inflater, container, false)
        binding.apply {
            editDescFragmentTimeYear.setText(String.format("%04d", editViewModel.dueYear))
            editDescFragmentTimeMonth.setText(String.format("%02d", editViewModel.dueMonth + 1))
            editDescFragmentTimeDate.setText(String.format("%02d", editViewModel.dueDate))
            editDescFragmentTimeHour.setText(String.format("%02d", editViewModel.dueHour))
            editDescFragmentTimeMinute.setText(String.format("%02d", editViewModel.dueMinutes))

            editDescFragmentTimeYear.setJumpToOnValidate(4, editDescFragmentTimeMonth,
                validator = { s, _, _, _ ->
                    val parsedYear = (s.toString().toIntOrNull() ?: -1)
                    if (parsedYear > 1898) {
                        editViewModel.dueYear = parsedYear
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
                    editViewModel.dueMonth = parsedMonth - 1
                    true
                } else false
            }

            editDescFragmentTimeDate.setJumpToOnValidate(
                2,
                editDescFragmentTimeHour
            ) { s, _, _, _ ->
                val parsedDate = (s.toString().toIntOrNull() ?: -1)
                if (parsedDate in 1..(editViewModel.dueMonth.daysInThisMonth(editViewModel.dueYear))) {
                    editViewModel.dueDate = parsedDate
                    true
                } else false
            }
            editDescFragmentTimeHour.setJumpToOnValidate(
                2,
                editDescFragmentTimeMinute
            ) { s, _, _, _ ->
                val parsedHour = (s.toString().toIntOrNull() ?: -1)
                if (parsedHour in 0..23) {
                    editViewModel.dueHour = parsedHour
                    true
                } else false
            }

            editDescFragmentTimeMinute.setOnTextChangedListener { s, start, before, count ->
                val parsedM = (s.toString().toIntOrNull() ?: -1)
                val valid = parsedM in 0..59
                if (valid) editViewModel.dueMinutes = parsedM
                else editDescFragmentTimeMinute.selectAll()
            }

            editDescFragmentTimeDate.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeDate.setText(String.format("%02d", editViewModel.dueDate))
            }
            editDescFragmentTimeYear.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeYear.setText(String.format("%04d", editViewModel.dueYear))
            }
            editDescFragmentTimeMonth.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeMonth.setText(
                        String.format(
                            "%02d",
                            editViewModel.dueMonth + 1
                        )
                    )
            }
            editDescFragmentTimeHour.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeHour.setText(String.format("%02d", editViewModel.dueHour))
            }
            editDescFragmentTimeMinute.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    editDescFragmentTimeMinute.setText(
                        String.format(
                            "%02d",
                            editViewModel.dueMinutes
                        )
                    )
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editDescFragmentTimeDate.requestFocusAndShowSoftKeyboard(binding.root.context, true)
        binding.editDescFragmentTimeDate.selectAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}