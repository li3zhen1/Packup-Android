package org.engrave.packup

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.microsoft.fluentui.popupmenu.*
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
    }

    private fun popDeadlineFilterMenu(anchorView: View) {
        val items = arrayListOf(
            PopupMenuItem(
                id = 0,
                title = "按截止时间升序排列",
                iconResourceId = R.drawable.ic_fluent_chat_help_24_regular,
                showDividerBelow = true
            ),
            PopupMenuItem(
                id = 1,
                title = "按截止时间升序排列",
                iconResourceId = R.drawable.ic_fluent_chat_help_24_regular,
                showDividerBelow = true
            ),
            PopupMenuItem(
                id = 2,
                title = "按课程名称排列",
                iconResourceId = R.drawable.ic_fluent_chat_help_24_regular
            )
        )
        val onPopupMenuItemClickListener = object : PopupMenuItem.OnClickListener {
            override fun onPopupMenuItemClicked(popupMenuItem: PopupMenuItem) {
                mainViewModel.deadlineSortOrder.value = when(popupMenuItem.id){
                    0 -> DeadlineSortOrder.DUE_TIME_ASCENDING
                    1 -> DeadlineSortOrder.DUE_TIME_DESCENDING
                    2 -> DeadlineSortOrder.SOURCE_COURSE_NAME
                    else -> DeadlineSortOrder.DUE_TIME_ASCENDING
                }
            }
        }
        val popupMenu = PopupMenu(this, anchorView, items, PopupMenu.ItemCheckableBehavior.NONE)
        popupMenu.onItemClickListener = onPopupMenuItemClickListener
        popupMenu.show()
    }
}
