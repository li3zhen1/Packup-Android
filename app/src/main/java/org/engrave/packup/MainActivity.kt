package org.engrave.packup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.ActivityMainBinding
import org.engrave.packup.ui.deadline.DeadlineFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainActivityToolBarContainer)

        binding.mainActivityToolbarTitle.text = ""

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
                        else -> DeadlineFragment()
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

        binding.mainActivityBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.deadline_fragment_item -> binding.mainActivityViewPager.setCurrentItem(
                    0,
                    false
                )
                R.id.event_fragment_item -> binding.mainActivityViewPager.setCurrentItem(1, false)
                R.id.document_fragment_item -> binding.mainActivityViewPager.setCurrentItem(
                    2,
                    false
                )
            }
            true
        }
    }
}
