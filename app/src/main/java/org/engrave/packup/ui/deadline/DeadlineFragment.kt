package org.engrave.packup.ui.deadline

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.EditDeadlineActivity
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineFilter
import org.engrave.packup.ui.main.MainViewModel
import org.engrave.packup.util.SimpleCountDown
import org.engrave.packup.worker.NEWLY_CRAWLED_DEADLINE_NUM

@AndroidEntryPoint
class DeadlineFragment() : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var addButton: ImageButton
    lateinit var messageBarOperationContainer: ConstraintLayout
    lateinit var messageBarNewlySyncedContainer: ConstraintLayout
    lateinit var messageTextNewlySynced: TextView
    lateinit var messageTextOperation: TextView
    lateinit var messageButtonWithdrawOperation: TextView

    lateinit var vibrator: Vibrator

    lateinit var deadlineAdapter: DeadlineListAdapter

    //    var deadlineItemDecorator = object : RecyclerView.ItemDecoration() {
//        override fun getItemOffsets(
//            outRect: Rect,
//            view: View,
//            parent: RecyclerView,
//            state: RecyclerView.State
//        ) {
//            super.getItemOffsets(outRect, view, parent, state)
//            if (context != null)
//                outRect.bottom = 48.inDp(context!!)
//        }
//    }
    lateinit var touchHelper: ItemTouchHelper
    lateinit var celebrateImage: ImageView
    lateinit var celebrateText: TextView

    private val deadlineViewModel: DeadlineViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val messageBarNewlySyncedDismissTimer = SimpleCountDown(3000) {
        messageBarNewlySyncedContainer.visibility = View.GONE
    }
    private val messageBarOperationDismissTimer = SimpleCountDown(3000) {
        messageBarOperationContainer.visibility = View.GONE
    }
    private val scrollToTopCountDown = SimpleCountDown(320) {
        recyclerView.smoothScrollToPosition(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_deadline, container, false).apply {
        recyclerView = findViewById(R.id.deadline_fragment_recyclerview)
        addButton = findViewById(R.id.deadline_fragment_add_button)
        messageBarOperationContainer =
            findViewById(R.id.deadline_fragment_message_bar_operation_container)
        messageBarNewlySyncedContainer =
            findViewById(R.id.deadline_fragment_message_bar_new_container)
        messageTextNewlySynced =
            findViewById(R.id.deadline_fragment_message_bar_new_text)
        messageTextOperation = findViewById(R.id.deadline_fragment_message_bar_operation_text)
        messageButtonWithdrawOperation =
            findViewById(R.id.deadline_fragment_message_bar_operation_withdraw)
        celebrateImage = findViewById(R.id.deadline_fragment_celebrate_image)
        celebrateText = findViewById(R.id.deadline_fragment_celebrate_text)
        celebrateImage.visibility = View.GONE
        celebrateText.visibility = View.INVISIBLE

        messageBarOperationContainer.visibility = View.GONE
        messageBarNewlySyncedContainer.visibility = View.GONE

        vibrator = activity?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

        val deadlineLayoutManager = LinearLayoutManager(activity)
        deadlineAdapter = DeadlineListAdapter(
            requireActivity(),
            context,
            onClickStar = { uid, isStarred ->
                if (isStarred)
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            120,
                            24
                        )
                    )
                deadlineViewModel.setStarred(uid, isStarred)
            },
            onClickComplete = { uid, isCompleted ->
                if (isCompleted) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            120,
                            48
                        )
                    )
                    SimpleCountDown(600) {
                        onSetDeadlineCompleted(uid, true)
                    }.start()
                } else onSetDeadlineCompleted(uid, false)
            },
            onClickRestore = { uid, isDeleted ->
                onSetDeadlineDeleted(uid, isDeleted)
            },
            onSwipeComplete = { uid ->
                onSetDeadlineCompleted(uid, true)
            },
            onSwipeDelete = { uid ->
                onSetDeadlineDeleted(uid, true)
            }
        )

        touchHelper = ItemTouchHelper(DeadlineItemTouchHelper(deadlineAdapter, context))
        addButton.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    EditDeadlineActivity::class.java
                )
            )
        }
        recyclerView.apply {
            layoutManager = deadlineLayoutManager
            adapter = deadlineAdapter
        }

        deadlineViewModel.apply {
            sortedDeadlines.observe(viewLifecycleOwner) {
                celebrateText.visibility = View.INVISIBLE
                celebrateImage.visibility = View.GONE
                it?.let {
                    deadlineAdapter.submitList(it + DeadlinePadding(it.size))
                    if (it.isNullOrEmpty())
                        SimpleCountDown(600) {
                            if (sortedDeadlines.value.isNullOrEmpty()) {
                                celebrateText.visibility = View.VISIBLE
                                celebrateImage.visibility = View.VISIBLE
                                if (filter.value == DeadlineFilter.PENDING_TO_COMPLETE) {
                                    celebrateText.text = "似乎已经处理完了所有 Deadline"
                                    celebrateImage.background =
                                        ContextCompat.getDrawable(context, R.drawable.ic_celebrate)
                                } else {
                                    celebrateText.text = "这里似乎没有什么东西"
                                    celebrateImage.background =
                                        ContextCompat.getDrawable(context, R.drawable.ic_cactus)
                                }
                            }
                        }.start()
                }
            }
            filter.observe(viewLifecycleOwner) {
                addButton.visibility =
                    if (it == DeadlineFilter.PENDING_TO_COMPLETE) View.VISIBLE else View.GONE
            }
        }
        mainViewModel.apply {
            deadlineSortOrder.observe(viewLifecycleOwner) {
                deadlineViewModel.sortOrder.value = it
                scrollToTopCountDown.restart()
            }
            deadlineFilter.observe(viewLifecycleOwner) {
                if (it == DeadlineFilter.PENDING_TO_COMPLETE)
                    touchHelper.attachToRecyclerView(recyclerView)
                else touchHelper.attachToRecyclerView(null)
                deadlineViewModel.filter.value = it
                scrollToTopCountDown.restart()
            }
        }
        activity?.let { activity ->
            WorkManager.getInstance(activity.application)
                .getWorkInfoByIdLiveData(deadlineViewModel.deadlineCrawlerRef.id)
                .observe(viewLifecycleOwner) {
                    val newlyCrawledCount = it.progress.getInt(NEWLY_CRAWLED_DEADLINE_NUM, -1)
                    if (newlyCrawledCount > 0) {
                        messageTextNewlySynced.text = "同步了 $newlyCrawledCount 项新的 Deadline。"
                        messageBarNewlySyncedContainer.visibility = View.VISIBLE
                        messageBarNewlySyncedDismissTimer.restart()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 防止内存泄露
        messageBarNewlySyncedDismissTimer.cancel()
        messageBarOperationDismissTimer.cancel()
        scrollToTopCountDown.cancel()
    }

    private fun onSetDeadlineCompleted(uid: Int, isCompleted: Boolean) {
        deadlineViewModel.setDeadlineCompleted(uid, isCompleted)
        messageTextOperation.text =
            if (!deadlineViewModel.getDeadlineByUid(uid)?.name.isNullOrBlank()) {
                with(deadlineViewModel.getDeadlineByUid(uid)?.name!!) {
                    getString(
                        if (isCompleted) R.string.deadline_has_been_completed
                        else R.string.deadline_has_been_completed_reverted,
                        if (this.length > 10) this.substring(0, 8) + "..."
                        else this
                    )
                }
            } else getString(
                if (isCompleted) R.string.deadline_has_been_completed
                else R.string.deadline_has_been_completed_reverted,
                "1 项 Deadline"
            )
        messageButtonWithdrawOperation.setOnClickListener {
            deadlineViewModel.setDeadlineCompleted(uid, !isCompleted)
            messageBarOperationContainer.visibility = View.GONE
        }
        messageBarOperationContainer.visibility = View.VISIBLE
        messageBarOperationDismissTimer.restart()
    }

    private fun onSetDeadlineDeleted(uid: Int, isDeleted: Boolean) {
        deadlineViewModel.setDeadlineDeleted(uid, isDeleted)
        messageTextOperation.text =
            if (!deadlineViewModel.getDeadlineByUid(uid)?.name.isNullOrBlank()) {
                with(deadlineViewModel.getDeadlineByUid(uid)?.name!!) {
                    getString(
                        if (isDeleted) R.string.deadline_has_been_deleted
                        else R.string.deadline_has_been_deleted_reverted,
                        if (this.length > 10) this.substring(0, 8) + "..."
                        else this
                    )
                }
            } else getString(
                if (isDeleted) R.string.deadline_has_been_deleted
                else R.string.deadline_has_been_deleted_reverted,
                "1 项 Deadline"
            )
        messageButtonWithdrawOperation.setOnClickListener {
            deadlineViewModel.setDeadlineDeleted(uid, !isDeleted)
            messageBarOperationContainer.visibility = View.GONE
        }
        messageBarOperationContainer.visibility = View.VISIBLE
        messageBarOperationDismissTimer.restart()
    }
}