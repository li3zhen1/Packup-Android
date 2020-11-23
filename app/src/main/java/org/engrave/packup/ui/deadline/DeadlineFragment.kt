package org.engrave.packup.ui.deadline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineSortOrder
import org.engrave.packup.ui.main.MainViewModel

@AndroidEntryPoint
class DeadlineFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var addButton: ImageButton
    private val deadlineViewModel: DeadlineViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_deadline, container, false).apply {
        recyclerView = findViewById(R.id.deadline_fragment_recyclerview)
        addButton = findViewById(R.id.deadline_fragment_add_button)
        val deadlineLayoutManager = LinearLayoutManager(activity)

        val deadlineAdapter = DeadlineListAdapter(context,
            onClickStar = { uid, boolean ->
                deadlineViewModel.setStarred(uid, boolean)
            }
        )

        addButton.setOnClickListener {

        }
        recyclerView.apply {
            layoutManager = deadlineLayoutManager
            adapter = deadlineAdapter
        }
        deadlineViewModel.apply {
            sortedDeadlines.observe(viewLifecycleOwner) {
                it?.let {
                    deadlineAdapter.submitList(it)
                }
            }
        }
        mainViewModel.apply {
            deadlineSortOrder.observe(viewLifecycleOwner){
                deadlineViewModel.sortOrder.value = it
            }

        }
    }
}