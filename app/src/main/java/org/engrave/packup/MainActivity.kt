package org.engrave.packup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.main_activity_bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.deadline_fragment_item -> viewPager.setCurrentItem(0, false)
//                R.id.event_fragment_item -> viewPager.setCurrentItem(1, false)
//                R.id.document_fragment_item -> viewPager.setCurrentItem(2, false)
//            }
            true
        }

    }
}
