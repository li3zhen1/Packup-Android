package org.engrave.packup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.FragmentEventBinding
import org.engrave.packup.ui.main.MainViewModel

@AndroidEntryPoint
class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter
    private lateinit var linearManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        eventAdapter = EventAdapter(requireContext()).apply {
            setHasStableIds(true)
        }
        linearManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL

        }
        binding.apply {
            eventsRecyclerView.apply {

                layoutManager = linearManager
                adapter = eventAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    var runnable = Runnable {
                        smoothScrollToPosition()
                    }

                    @Volatile
                    private var isUserControl = false
                    override fun onScrolled(r: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(r, dx, dy)
                        mainViewModel.eventViewTimeStamp.value = getChildItemId(getChildAt(0))
                        if (r.scrollState == RecyclerView.SCROLL_STATE_SETTLING && !isUserControl) {
                            if (dx in -3..3) {
                                r.stopScroll()
                            }
                        }
                    }



                    fun smoothScrollToPosition() {
                        isUserControl = true
                        val stickyInfoView = getChildAt(0)
                        val bottom = stickyInfoView.right
                        val height = stickyInfoView.measuredWidth
                        if (bottom != height) {
                            if (bottom >= (height / 2)) {
                                smoothScrollBy(-(height - bottom), 0)
                            } else {
                                smoothScrollBy(bottom, 0)
                            }
                        }
                    }

                    override fun onScrollStateChanged(r: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(r, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE)
                            if (!isUserControl)
                                postDelayed(runnable, 80)
                        if (r.scrollState != RecyclerView.SCROLL_STATE_SETTLING)
                            isUserControl = false
                    }
                })
            }
        }

/*        eventAdapter.postList(
            listOf(
                DailyEventsItem(
                    1,
                    listOf(),
                    listOf(
                        DailyCourseItem(
                            480,
                            590,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            610,
                            720,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            780,
                            890,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            910,
                            1020,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            1120,
                            1290,
                            0,
                            "Hello",
                            "classroom"
                        )
                    )
                ),
                DailyEventsItem(
                    2,
                    listOf(),
                    listOf(
                        DailyCourseItem(
                            480,
                            590,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            910,
                            1020,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            1120,
                            1290,
                            0,
                            "Hello",
                            "classroom"
                        )
                    )
                ),
                DailyEventsItem(
                    3,
                    listOf(),
                    listOf(

                        DailyCourseItem(
                            610,
                            720,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            780,
                            890,
                            0,
                            "Hello",
                            "classroom"
                        ),

                        DailyCourseItem(
                            1120,
                            1290,
                            0,
                            "Hello",
                            "classroom"
                        )
                    )
                ),
                DailyEventsItem(
                    1,
                    listOf(),
                    listOf(
                        DailyCourseItem(
                            480,
                            590,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            610,
                            720,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            780,
                            890,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            910,
                            1020,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            1120,
                            1290,
                            0,
                            "Hello",
                            "classroom"
                        )
                    )
                ),
                DailyEventsItem(
                    2,
                    listOf(),
                    listOf(
                        DailyCourseItem(
                            480,
                            590,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            910,
                            1020,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            1120,
                            1290,
                            0,
                            "Hello",
                            "classroom"
                        )
                    )
                ),
                DailyEventsItem(
                    3,
                    listOf(),
                    listOf(

                        DailyCourseItem(
                            610,
                            720,
                            0,
                            "Hello",
                            "classroom"
                        ),
                        DailyCourseItem(
                            780,
                            890,
                            0,
                            "Hello",
                            "classroom"
                        ),

                        DailyCourseItem(
                            1120,
                            1290,
                            0,
                            "Hello",
                            "classroom"
                        )
                    )
                )
            )
        )*/

        eventViewModel.eventList.observe(viewLifecycleOwner) {
            eventAdapter.postList(
                it
            )
        }

        mainViewModel.statusBarStatus.observe(viewLifecycleOwner) {

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}