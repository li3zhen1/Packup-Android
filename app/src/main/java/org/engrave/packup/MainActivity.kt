package org.engrave.packup

import android.app.Service
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.ActivityMainBinding
import org.engrave.packup.ui.deadline.DeadlineFragment
import org.engrave.packup.ui.deadline.DocumentFragment
import org.engrave.packup.ui.event.EventFragment
import org.engrave.packup.ui.filter.FilterBottomSheetFragment
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
                        1 -> EventFragment()
                        2 -> DocumentFragment()
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
            popDeadlineFilterMenu()
        }
        binding.mainActivityBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.deadline_fragment_item -> {
                    binding.mainActivityViewPager.setCurrentItem(
                        0,
                        true
                    )
                    mainViewModel.fragmentId.value = MainViewModel.FRAGMENT_ID_DEADLINE
                }
                R.id.event_fragment_item -> {
                    binding.mainActivityViewPager.setCurrentItem(
                        1,
                        true
                    )
                    mainViewModel.fragmentId.value = MainViewModel.FRAGMENT_ID_EVENT
                }
                R.id.document_fragment_item -> {
                    binding.mainActivityViewPager.setCurrentItem(
                        2,
                        true
                    )
                    mainViewModel.fragmentId.value = MainViewModel.FRAGMENT_ID_DOCUMENT
                }
            }
            true
        }
        mainViewModel.statusBarStatus.observe(this) {
            fun getButtonDrawable() =
                if (it == MainViewModel.STATUS_BAR_DEADLINE_DELETED)
                    ContextCompat.getDrawable(this, R.drawable.icon_button_on_status_bar_vibrant)
                else ContextCompat.getDrawable(this, R.drawable.icon_button_on_status_bar)
            binding.mainActivityToolBarBackground.background =
                if (it == MainViewModel.STATUS_BAR_DEADLINE_DELETED)
                    ContextCompat.getDrawable(this, R.color.colorVibrant)
                else ContextCompat.getDrawable(this, R.color.activityStatusBarColor)
            binding.mainActivitySearchButton.background = getButtonDrawable()
            binding.mainActivityFilterButton.background = getButtonDrawable()
            binding.mainActivityNavButton.background = getButtonDrawable()
            window.statusBarColor =
                if (it == MainViewModel.STATUS_BAR_DEADLINE_DELETED)
                    ContextCompat.getColor(this, R.color.colorVibrant)
                else ContextCompat.getColor(this, R.color.activityStatusBarColor)
            binding.mainActivityToolbarTitle.text = when (it) {
                MainViewModel.STATUS_BAR_DOCUMENT -> "文档"
                MainViewModel.STATUS_BAR_EVENT -> "事件"
                MainViewModel.STATUS_BAR_DEADLINE_COMPLETED -> "已完成的 Deadline"
                MainViewModel.STATUS_BAR_DEADLINE_DELETED -> "已删除的 Deadline"
                else -> "Deadline"
            }
            binding.mainActivityFilterButton.visibility =
                if (it <= MainViewModel.STATUS_BAR_DEADLINE_COMPLETED) View.VISIBLE else View.GONE
        }

    }

    private fun popDeadlineFilterMenu() = FilterBottomSheetFragment()
        .show(supportFragmentManager, "DEADLINE_FILTER_BOTTOM_SHEET")
}
