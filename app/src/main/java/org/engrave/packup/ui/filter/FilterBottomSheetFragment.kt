package org.engrave.packup.ui.filter

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineFilter
import org.engrave.packup.data.deadline.DeadlineSortOrder
import org.engrave.packup.databinding.FragmentSortOrderSelectionBinding
import org.engrave.packup.ui.main.MainViewModel
import org.engrave.packup.util.SimpleCountDown

class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSortOrderSelectionBinding? = null
    private val binding get() = _binding!!
    private val dismissCountDown = SimpleCountDown(160) { dismiss() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSortOrderSelectionBinding.inflate(inflater, container, false)
        fun getButtonDefaultDrawable() = context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.item_default_button_on_bottom_sheet
            )
        }

        fun getButtonSelectedDrawable() = context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.item_primary_button_on_bottom_sheet
            )
        }

        fun getButtonVibrantDrawable() = context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.item_vibrant_button_on_bottom_sheet
            )
        }

        fun getTextDefaultColor() = context?.let {
            ContextCompat.getColor(
                it,
                R.color.colorText
            )
        }!!


        fun getTextWhiteColor() = context?.let {
            ContextCompat.getColor(
                it,
                R.color.color_white
            )
        }!!
        mainViewModel.deadlineSortOrder.observe(viewLifecycleOwner) {
            binding.buttonSortTimeDescending.background =
                if (it == DeadlineSortOrder.DUE_TIME_DESCENDING) getButtonSelectedDrawable()
                else getButtonDefaultDrawable()
            binding.textSortTimeDescending.setTextColor(
                if (it == DeadlineSortOrder.DUE_TIME_DESCENDING) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconSortTimeDescending.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineSortOrder.DUE_TIME_DESCENDING) getTextWhiteColor()
                else getTextDefaultColor()
            )


            binding.buttonSortTimeAscending.background =
                if (it == DeadlineSortOrder.DUE_TIME_ASCENDING) getButtonSelectedDrawable()
                else getButtonDefaultDrawable()
            binding.textSortTimeAscending.setTextColor(
                if (it == DeadlineSortOrder.DUE_TIME_ASCENDING) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconSortTimeAscending.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineSortOrder.DUE_TIME_ASCENDING) getTextWhiteColor()
                else getTextDefaultColor()
            )


            binding.buttonSortImportance.background =
                if (it == DeadlineSortOrder.IMPORTANCE_DESCENDING) getButtonSelectedDrawable()
                else getButtonDefaultDrawable()
            binding.textSortImportance.setTextColor(
                if (it == DeadlineSortOrder.IMPORTANCE_DESCENDING) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconSortImportance.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineSortOrder.IMPORTANCE_DESCENDING) getTextWhiteColor()
                else getTextDefaultColor()
            )


            binding.buttonSortCourseNameAscending.background =
                if (it == DeadlineSortOrder.SOURCE_COURSE_NAME) getButtonSelectedDrawable()
                else getButtonDefaultDrawable()
            binding.textSortCourseNameAscending.setTextColor(
                if (it == DeadlineSortOrder.SOURCE_COURSE_NAME) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconSortCourseNameAscending.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineSortOrder.SOURCE_COURSE_NAME) getTextWhiteColor()
                else getTextDefaultColor()
            )
        }
        mainViewModel.deadlineFilter.observe(viewLifecycleOwner) {
            binding.buttonFilterDefault.background =
                if (it == DeadlineFilter.PENDING_TO_COMPLETE) getButtonSelectedDrawable()
                else getButtonDefaultDrawable()
            binding.textFilterDefault.setTextColor(
                if (it == DeadlineFilter.PENDING_TO_COMPLETE) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconFilterDefault.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineFilter.PENDING_TO_COMPLETE) getTextWhiteColor()
                else getTextDefaultColor()
            )

            binding.buttonFilterFinished.background =
                if (it == DeadlineFilter.COMPLETED) getButtonSelectedDrawable()
                else getButtonDefaultDrawable()
            binding.textFilterFinished.setTextColor(
                if (it == DeadlineFilter.COMPLETED) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconFilterFinished.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineFilter.COMPLETED) getTextWhiteColor()
                else getTextDefaultColor()
            )

            binding.buttonFilterDeleted.background =
                if (it == DeadlineFilter.DELETED) getButtonVibrantDrawable()
                else getButtonDefaultDrawable()
            binding.textFilterDeleted.setTextColor(
                if (it == DeadlineFilter.DELETED) getTextWhiteColor()
                else getTextDefaultColor()
            )
            binding.iconFilterDeleted.imageTintList = ColorStateList.valueOf(
                if (it == DeadlineFilter.DELETED) getTextWhiteColor()
                else getTextDefaultColor()
            )
        }
        binding.apply {
            buttonSortTimeDescending.setOnClickListener {
                if (mainViewModel.deadlineSortOrder.value != DeadlineSortOrder.DUE_TIME_DESCENDING) {
                    mainViewModel.deadlineSortOrder.value = DeadlineSortOrder.DUE_TIME_DESCENDING
                    dismissCountDown.restart()
                }
            }
            buttonSortTimeAscending.setOnClickListener {
                if (mainViewModel.deadlineSortOrder.value != DeadlineSortOrder.DUE_TIME_ASCENDING) {
                    mainViewModel.deadlineSortOrder.value = DeadlineSortOrder.DUE_TIME_ASCENDING
                    dismissCountDown.restart()
                }
            }
            buttonSortCourseNameAscending.setOnClickListener {
                if (mainViewModel.deadlineSortOrder.value != DeadlineSortOrder.SOURCE_COURSE_NAME) {
                    mainViewModel.deadlineSortOrder.value = DeadlineSortOrder.SOURCE_COURSE_NAME
                    dismissCountDown.restart()
                }
            }
            buttonSortImportance.setOnClickListener {
                if (mainViewModel.deadlineSortOrder.value != DeadlineSortOrder.IMPORTANCE_DESCENDING) {
                    mainViewModel.deadlineSortOrder.value = DeadlineSortOrder.IMPORTANCE_DESCENDING
                    dismissCountDown.restart()
                }
            }

            buttonFilterDefault.setOnClickListener {
                if (mainViewModel.deadlineFilter.value != DeadlineFilter.PENDING_TO_COMPLETE) {
                    mainViewModel.deadlineFilter.value = DeadlineFilter.PENDING_TO_COMPLETE
                    dismissCountDown.restart()
                }
            }
            buttonFilterFinished.setOnClickListener {
                if (mainViewModel.deadlineFilter.value != DeadlineFilter.COMPLETED) {
                    mainViewModel.deadlineFilter.value = DeadlineFilter.COMPLETED
                    dismissCountDown.restart()
                }
            }
            buttonFilterDeleted.setOnClickListener {
                if (mainViewModel.deadlineFilter.value != DeadlineFilter.DELETED) {
                    mainViewModel.deadlineFilter.value = DeadlineFilter.DELETED
                    dismissCountDown.restart()
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dismissCountDown.cancel()
    }
}