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
import org.engrave.packup.util.*
import ws.vinta.pangu.Pangu
import java.util.*
import kotlin.math.floor


const val DEADLINE_DETAIL_ACTIVITY_UID = "DEADLINE_DETAIL_ACTIVITY_UID"

@AndroidEntryPoint
class DeadlineDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeadlineDetailBinding
    private val detailViewModel: DeadlineDetailViewModel by viewModels()
    private val pangu = Pangu()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewModel.setDeadlineUid(intent.getIntExtra(DEADLINE_DETAIL_ACTIVITY_UID, -1))
        binding = ActivityDeadlineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.deadlineDetailActivityToolBarContainer)
        val detailLayoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        val attachedFilesAdapter = AttachedFilesAdapter(this, listOf())
        binding.deadlineDetailLinkedFileList.apply {
            layoutManager = detailLayoutManager
            adapter = attachedFilesAdapter
        }
        binding.deadlineDetailStarButton.setOnClickListener {
            detailViewModel.alterStarredAsync()
        }

        detailViewModel.deadline.observe(this){
            if (it != null)
                binding.apply {
                    /**
                     * 动作处理放外面，这里只放界面绑定！！
                     */
                    deadlineDetailTitle.text = it.name.getSpaced(pangu)
                    deadlineDetailSourceLinkText.text = it.source_course_name_without_semester

                    it.description.let { desc ->
                        if (desc.isNullOrBlank()) {
                            deadlineDetailDescriptionContent.visibility = View.GONE
                            deadlineDetailDescBlockCaption.text = "添加备注"
                        }
                        else {
                            deadlineDetailDescriptionContent.visibility = View.VISIBLE
                            deadlineDetailDescBlockCaption.text = "备注"
                            deadlineDetailDescriptionContent.text =
                                desc.split("\n").filter { ts -> ts.isNotBlank() }.joinToString("\n")
                        }
                    }

                    if (it.attached_file_list.isNullOrEmpty()) {
                        deadlineDetailLinkedFileList.visibility = View.GONE
                        deadlineDetailAttachFileCaption.text = "没有附件"
                    } else {
                        deadlineDetailLinkedFileList.visibility = View.VISIBLE
                        deadlineDetailAttachFileCaption.text = "添加的附件"
                        attachedFilesAdapter.postFileList(listOf(null) + it.attached_file_list + null)
                    }

                    deadlineDetailStarButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@DeadlineDetailActivity,
                            if (it.is_starred) R.drawable.outline_button_star_filled
                            else R.drawable.ic_packup_star_24_white_border
                        )
                    )
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

                    deadlineDetailDueButton.apply {
                        text = Calendar.getInstance().apply {
                            time = Date(it.due_time ?: 0)
                        }.toGlobalizedString(
                            context,
                            autoOmitYear = false,
                            omitTime = false,
                            omitWeek = false
                        )
                        isClickable = (it.course_object_id == null)
                    }
                    deadlineDetailActivityReminderButton.apply {
                        text = if (it.reminder != null) {
                            Calendar.getInstance().apply {
                                time = Date(it.reminder)
                            }.toGlobalizedString(
                                context,
                                autoOmitYear = false,
                                omitTime = false,
                                omitWeek = false
                            )
                        } else "设置提醒时间"
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                if (it.reminder != null)
                                    R.color.colorHeroText
                                else R.color.colorText
                            )
                        )
                    }
                    deadlineDetailActivityLinkCourseButton.apply {
                        if (it.source_name.isNullOrBlank()) {
                            text = "链接到课程"
                            setTextColor(ContextCompat.getColor(context, R.color.colorText))
                        } else {
                            text = it.source_name
                            setTextColor(ContextCompat.getColor(context, R.color.colorHeroText))
                        }
                        isClickable = (it.course_object_id == null)
                    }
                }
        }
        binding.apply {
            deadlineDetailNavButton.setOnClickListener {
                finish()
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("sViewY",binding.scrollContainer.scrollY)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        binding.scrollContainer.scrollTo(0, savedInstanceState.getInt("sViewY"))
        super.onRestoreInstanceState(savedInstanceState)
    }
}