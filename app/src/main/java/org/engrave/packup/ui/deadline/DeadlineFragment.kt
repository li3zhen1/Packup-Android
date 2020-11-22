package org.engrave.packup.ui.deadline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.R

@AndroidEntryPoint
class DeadlineFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    private val deadlineViewModel: DeadlineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_deadline, container, false).apply {
        recyclerView = findViewById(R.id.deadline_fragment_recyclerview)
        val deadlineLayoutManager = LinearLayoutManager(activity)
        val deadlineAdapter =  DeadlineListAdapter(context)
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