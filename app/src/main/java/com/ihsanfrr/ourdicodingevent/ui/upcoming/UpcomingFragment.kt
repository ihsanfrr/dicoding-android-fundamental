package com.ihsanfrr.ourdicodingevent.ui.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ihsanfrr.ourdicodingevent.R
import com.ihsanfrr.ourdicodingevent.adapter.ListDicodingEventAdapter
import com.ihsanfrr.ourdicodingevent.databinding.FragmentUpcomingBinding
import com.ihsanfrr.ourdicodingevent.helpers.ConnectionHelper
import com.ihsanfrr.ourdicodingevent.ui.MainViewModel

class UpcomingFragment : Fragment() {

    private var binding: FragmentUpcomingBinding? = null
    private lateinit var listrecyclerView: RecyclerView
    private lateinit var searchViewUpcoming: SearchView
    private lateinit var listAdapter: ListDicodingEventAdapter
    private lateinit var progressBar: ProgressBar
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewUpcoming = binding!!.searchViewUpcoming
        searchViewUpcoming.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchEvents(1, query)
                } else {
                    viewModel.fetchEvents(1)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.fetchEvents(1)
                    viewModel.clearError()
                }
                return true
            }
        })

        progressBar = binding!!.progressBar
        listrecyclerView = binding!!.upcomingListRecyclerView

        listAdapter = ListDicodingEventAdapter(
            onClick = { id ->
                navigateToDetail(id)
            }
        )
        listrecyclerView.adapter = listAdapter

        listrecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            listAdapter.setEvents(events)
        }
        viewModel.searchResults.observe(viewLifecycleOwner) { events ->
            listAdapter.setEvents(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                listrecyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                listrecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchEvents(1)
    }

    override fun onResume() {
        super.onResume()
        ConnectionHelper.verifyInternet(requireContext())
        searchViewUpcoming.setQuery(null, false)
        searchViewUpcoming.clearFocus()
        searchViewUpcoming.isIconified = true
        viewModel.fetchEvents(1)
    }

    private fun navigateToDetail(eventId: Int?) {
        eventId?.let {
            val action = UpcomingFragmentDirections.actionNavigationUpcomingToDetailFragment(it)
            findNavController().navigate(action)
        }
    }
}