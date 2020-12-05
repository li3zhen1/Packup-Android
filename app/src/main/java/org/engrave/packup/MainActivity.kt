package org.engrave.packup

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.component.menu.FloatingMenu
import org.engrave.packup.component.menu.FloatingMenuItem
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
            popDeadlineFilterMenu(it)
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

        mainViewModel.deadlineSortOrder.observe(this) {
            binding.mainActivityFilterButtonText.apply {
                text = it.toString()
                visibility =
                    if (it == DeadlineSortOrder.DUE_TIME_ASCENDING) View.GONE else View.VISIBLE
            }
        }
    }

    private fun popDeadlineFilterMenu(anchorView: View) {
        val items = arrayListOf(
            FloatingMenuItem(
                R.id.menu_deadline_filter_due_time_ascending,
                "asgduy",
                R.drawable.ic_fluent_chat_help_24_regular
            ),
            FloatingMenuItem(
                R.id.menu_deadline_filter_due_time_descending,
                "dhdfuyidug",
                R.drawable.ic_fluent_chat_help_24_regular
            ),
            FloatingMenuItem(
                R.id.menu_deadline_filter_course_name,
                "sgjfgjyusad",
                R.drawable.ic_fluent_chat_help_24_regular
            )
        )
        val onPopupMenuItemClickListener = object : FloatingMenuItem.OnClickListener {
            override fun onFloatingMenuItemClicked(floatingMenuItem: FloatingMenuItem) {
                Toast.makeText(applicationContext, floatingMenuItem.title, Toast.LENGTH_SHORT).show()
            }
        }

        val popupMenu = FloatingMenu(this, anchorView, items,FloatingMenu.ItemCheckableBehavior.NONE)
        popupMenu.onItemClickListener = onPopupMenuItemClickListener
        popupMenu.show()
    //        val popupMenu = PopupMenu(this, anchorView)
//        popupMenu.apply {
//            menuInflater.inflate(R.menu.deadline_sort_order, popupMenu.menu)
//            setOnMenuItemClickListener {
//                mainViewModel.deadlineSortOrder.value = when (it.itemId) {
//                    R.id.menu_deadline_filter_due_time_ascending -> DeadlineSortOrder.DUE_TIME_ASCENDING
//                    R.id.menu_deadline_filter_due_time_descending -> DeadlineSortOrder.DUE_TIME_DESCENDING
//                    R.id.menu_deadline_filter_course_name -> DeadlineSortOrder.SOURCE_COURSE_NAME
//                    else -> DeadlineSortOrder.DUE_TIME_ASCENDING
//                }
//                true
//            }
//            setOnDismissListener {
//                // 控件消失时的事件
//            }
//            show()
//        }
    }
}
