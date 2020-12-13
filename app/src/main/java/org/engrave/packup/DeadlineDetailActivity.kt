package org.engrave.packup

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.ActivityDeadlineDetailBinding
import org.engrave.packup.ui.detail.AttachedFilesAdapter
import org.engrave.packup.ui.detail.DeadlineDetailViewModel
import org.engrave.packup.ui.detail.DeadlineItemAttachedFileItem


const val DEADLINE_DETAIL_ACTIVITY_UID = "DEADLINE_DETAIL_ACTIVITY_UID"

@AndroidEntryPoint
class DeadlineDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeadlineDetailBinding
    private val detailViewModel: DeadlineDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewModel.setDeadlineUid(intent.getIntExtra(DEADLINE_DETAIL_ACTIVITY_UID, -1))

        binding = ActivityDeadlineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.deadlineDetailActivityToolBarContainer)
        val detailLayoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.deadlineDetailLinkedFileList.apply {
            layoutManager = detailLayoutManager
            adapter = AttachedFilesAdapter(
                this@DeadlineDetailActivity,
                listOf(
                    DeadlineItemAttachedFileItem(
                        "Hello.pdf",
                        0,
                        ""
                    ),
                    DeadlineItemAttachedFileItem(
                        "Hello.doc",
                        1,
                        ""
                    ),
                    DeadlineItemAttachedFileItem(
                        "Hello.png",
                        2,
                        ""
                    ),
                    DeadlineItemAttachedFileItem(
                        "Hello.svg",
                        3,
                        ""
                    )
                )
            )
        }

        detailViewModel.deadline.observe(this){
            binding.apply {
                deadlineDetailTitle.text = it.name
                deadlineDetailSourceLinkText.text = it.source_course_name_without_semester
            }
        }
        binding.apply {
            deadlineDetailNavButton.setOnClickListener {
                finish()
            }
        }
    }


}