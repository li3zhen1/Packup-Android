package org.engrave.packup.ui.deadline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineSortOrder

@AndroidEntryPoint
class DeadlineFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var addButton: ImageButton
    private val deadlineViewModel: DeadlineViewModel by viewModels()

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
        val deadlineAdapter =  DeadlineListAdapter(context)
        addButton.setOnClickListener {
            deadlineViewModel.sortOrder.value = DeadlineSortOrder.DUE_TIME_DESCENDING
        }
        recyclerView.apply {
            layoutManager = deadlineLayoutManager
            adapter = deadlineAdapter
        }
        deadlineViewModel.apply {
            sortedDeadlines.observe(viewLifecycleOwner){
                it?.let {
                    deadlineAdapter.submitList(it)
                }
            }
        }
    }
}