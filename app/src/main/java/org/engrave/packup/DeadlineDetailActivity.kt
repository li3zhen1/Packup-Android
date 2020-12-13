package org.engrave.packup

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.ActivityDeadlineDetailBinding
import org.engrave.packup.ui.detail.AttachedFilesAdapter
import org.engrave.packup.ui.detail.DeadlineDetailViewModel
import org.engrave.packup.ui.detail.DeadlineItemAttachedFileItem
import org.engrave.packup.util.DAY_IN_MILLIS
import org.engrave.packup.util.HOUR_IN_MILLIS
import org.engrave.packup.util.WEEK_IN_MILLIS
import java.util.*
import kotlin.math.floor


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


                val remainingTime = it.due_time?.minus(Date().time)
                deadlineDetailRemainingTimeText.apply {
                    if (it.has_submission) {
                        text = "已提交"
                        deadlineStatusCaption.visibility = View.VISIBLE
                        deadlineDetailPillPlaceholder.visibility = View.VISIBLE
                        deadlineStatusCaption.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.pill_safe_green
                            )
                    } else {
                        when {
                            remainingTime == null -> {
                                visibility = View.GONE
                                deadlineStatusCaption.visibility = View.GONE
                                deadlineDetailPillPlaceholder.visibility = View.GONE
                            }
                            remainingTime <= 0 -> {
                                text = "已逾期"
                                deadlineStatusCaption.visibility = View.VISIBLE
                                deadlineDetailPillPlaceholder.visibility = View.VISIBLE
                                deadlineStatusCaption.background =
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.pill_warning_purple
                                    )
                            }
                            remainingTime < DAY_IN_MILLIS -> {
                                text =
                                    "剩余 ${floor(remainingTime.toDouble() / HOUR_IN_MILLIS).toInt()} 小时"
                                deadlineStatusCaption.visibility = View.VISIBLE
                                deadlineDetailPillPlaceholder.visibility = View.VISIBLE
                                deadlineStatusCaption.background =
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.pill_warning_red
                                    )
                            }
                            remainingTime < WEEK_IN_MILLIS -> {
                                text =
                                    "剩余 ${floor(remainingTime.toDouble() / DAY_IN_MILLIS).toInt()} 天"
                                deadlineStatusCaption.visibility = View.VISIBLE
                                deadlineDetailPillPlaceholder.visibility = View.VISIBLE
                                deadlineStatusCaption.background =
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.pill_warning_orange
                                    )
                            }
                            else -> {
                                visibility = View.GONE
                                deadlineStatusCaption.visibility = View.GONE
                                deadlineDetailPillPlaceholder.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
        binding.apply {
            deadlineDetailNavButton.setOnClickListener {
                finish()
            }
        }
    }


}