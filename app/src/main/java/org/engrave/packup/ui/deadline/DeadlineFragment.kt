package org.engrave.packup.ui.deadline

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import org.engrave.packup.util.inDp
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

    lateinit var deadlineAdapter: DeadlineListAdapter
    var deadlineItemDecorator = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (context != null)
                outRect.bottom = 48.inDp(context!!)
        }
    }
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
    private val scrollToTopCountDown = SimpleCountDown(400) {
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

        messageBarOperationContainer.visibility = View.GONE
        messageBarNewlySyncedContainer.visibility = View.GONE

        val deadlineLayoutManager = LinearLayoutManager(activity)
        deadlineAdapter = DeadlineListAdapter(
            context,
            onClickStar = { uid, isStarred ->
                deadlineViewModel.setStarred(uid, isStarred)
            },
            onClickComplete = { uid, isCompleted ->
                deadlineViewModel.setDeadlineCompleted(uid, isCompleted)
            },
            onAttemptCompleteItem = { uid ->
                deadlineViewModel.setDeadlineCompleted(uid, true)
                messageTextOperation.text =
                    if (!deadlineViewModel.getDeadlineByUid(uid)?.name.isNullOrBlank()) {
                        with(deadlineViewModel.getDeadlineByUid(uid)?.name!!) {
                            getString(
                                R.string.deadline_has_been_completed,
                                if (this.length > 10) this.substring(0, 8) + "..."
                                else this
                            )
                        }
                    } else getString(
                        R.string.deadline_has_been_completed,
                        "1 项 Deadline"
                    )
                messageButtonWithdrawOperation.setOnClickListener {
                    deadlineViewModel.setDeadlineCompleted(uid, false)
                    messageBarOperationContainer.visibility = View.GONE
                }
                messageBarOperationContainer.visibility = View.VISIBLE
                messageBarOperationDismissTimer.restart()
            },
            onAttemptDeleteItem = { uid ->
                deadlineViewModel.setDeadlineDeleted(uid, true)
                messageTextOperation.text =
                    if (!deadlineViewModel.getDeadlineByUid(uid)?.name.isNullOrBlank()) {
                        with(deadlineViewModel.getDeadlineByUid(uid)?.name!!) {
                            getString(
                                R.string.deadline_has_been_deleted,
                                if (this.length > 10) this.substring(0, 8) + "..."
                                else this
                            )
                        }
                    } else getString(
                        R.string.deadline_has_been_deleted,
                        "1 项 Deadline"
                    )
                messageButtonWithdrawOperation.setOnClickListener {
                    deadlineViewModel.setDeadlineDeleted(uid, false)
                    messageBarOperationContainer.visibility = View.GONE
                }
                messageBarOperationContainer.visibility = View.VISIBLE
                messageBarOperationDismissTimer.restart()
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
                it?.let {
                    deadlineAdapter.submitList(it + DeadlinePadding(it.size))
                    if (it.isEmpty()) {
                        celebrateText.visibility = View.VISIBLE
                        celebrateImage.visibility = View.VISIBLE
                    } else {
                        celebrateText.visibility = View.GONE
                        celebrateImage.visibility = View.GONE
                    }
                }
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
}