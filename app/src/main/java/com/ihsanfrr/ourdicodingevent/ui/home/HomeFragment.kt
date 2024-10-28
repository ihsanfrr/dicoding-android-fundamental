package com.ihsanfrr.ourdicodingevent.ui.home

import android.view.View
import android.os.Bundle
import android.widget.Toast
import android.view.ViewGroup
import android.widget.ProgressBar
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.ihsanfrr.ourdicodingevent.ui.MainViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihsanfrr.ourdicodingevent.adapter.DicodingEventAdapter
import com.ihsanfrr.ourdicodingevent.databinding.FragmentHomeBinding
import com.ihsanfrr.ourdicodingevent.ui.ViewModelFactory
import com.ihsanfrr.ourdicodingevent.ui.setting.SettingPreferences
import com.ihsanfrr.ourdicodingevent.ui.setting.dataStore
import com.ihsanfrr.ourdicodingevent.data.Result

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var gridRecyclerView: RecyclerView
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var gridAdapter: DicodingEventAdapter
    private lateinit var listAdapter: DicodingEventAdapter
    private lateinit var progressBar: ProgressBar

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridRecyclerView = binding.gridRecyclerView
        listRecyclerView = binding.listRecyclerView
        progressBar = binding.progressBar

        gridAdapter = DicodingEventAdapter(viewModel, false)
        listAdapter = DicodingEventAdapter(viewModel, true)

        gridRecyclerView.adapter = gridAdapter
        listRecyclerView.adapter = listAdapter

        gridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        observeGridRecycler()
        observeListRecycler()
    }

    private fun observeGridRecycler(){
        viewModel.fetchActiveEvents(requireContext()).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        gridAdapter.setEvents(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Error: " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun observeListRecycler() {
        viewModel.fetchInactiveEvents(requireContext()).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        listAdapter.setEvents(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}