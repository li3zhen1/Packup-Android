package org.engrave.packup

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.data.deadline.DeadlineSortOrder
import org.engrave.packup.databinding.ActivityMainBinding
import org.engrave.packup.ui.deadline.DeadlineFragment
import org.engrave.packup.ui.event.EventFragment
import org.engrave.packup.ui.main.MainViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainActivityToolBarContainer)

        binding.mainActivityToolbarTitle.text = "Deadline"

        binding.mainActivityViewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 2
            adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount(): Int {
                    return 3
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> DeadlineFragment()
                        else -> EventFragment()
                    }
                }
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.mainActivityBottomNav.menu.getItem(position).isChecked = true
                }
            })
        }

        binding.mainActivityFilterButton.setOnClickListener {
            binding.mainActivityDeadlineFilterContainer.apply {
                visibility = if (visibility == View.GONE) View.VISIBLE
                else View.GONE
            }
        }

        binding.mainActivityDeadlineFilterDropper.apply {
            setOnClickListener {
                popDeadlineFilterMenu(it)
            }
        }

        binding.mainActivityBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.deadline_fragment_item -> binding.mainActivityViewPager.setCurrentItem(
                    0,
                    false
                )
                R.id.event_fragment_item -> binding.mainActivityViewPager.setCurrentItem(
                    1,
                    false
                )
                R.id.document_fragment_item -> binding.mainActivityViewPager.setCurrentItem(
                    2,
                    false
                )
            }
            true
        }

        mainViewModel.deadlineSortOrder.observe(this){
            binding.mainActivityDeadlineFilterDropper.text = it.toString()
        }
    }

    private fun popDeadlineFilterMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.deadline_sort_order, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            mainViewModel.deadlineSortOrder.value = when(it.itemId){
                R.id.menu_deadline_filter_due_time_ascending -> DeadlineSortOrder.DUE_TIME_ASCENDING
                R.id.menu_deadline_filter_due_time_descending -> DeadlineSortOrder.DUE_TIME_DESCENDING
                R.id.menu_deadline_filter_course_name -> DeadlineSortOrder.SOURCE_COURSE_NAME
                else -> DeadlineSortOrder.DUE_TIME_ASCENDING
            }
            true
        }
        popupMenu.setOnDismissListener {
            // 控件消失时的事件
        }
    }
}
