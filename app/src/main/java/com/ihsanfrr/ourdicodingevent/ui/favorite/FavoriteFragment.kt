package com.ihsanfrr.ourdicodingevent.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ihsanfrr.ourdicodingevent.adapter.DicodingEventAdapter
import com.ihsanfrr.ourdicodingevent.databinding.FragmentFavoriteBinding
import com.ihsanfrr.ourdicodingevent.ui.MainViewModel
import com.ihsanfrr.ourdicodingevent.ui.ViewModelFactory
import com.ihsanfrr.ourdicodingevent.ui.setting.SettingPreferences
import com.ihsanfrr.ourdicodingevent.ui.setting.dataStore

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listAdapter: DicodingEventAdapter

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.progressBar
        listRecyclerView = binding.favoriteListRecyclerView
        setupRecyclerView()
        observeFavoritesEvents()
    }

    private fun setupRecyclerView() {
        listAdapter = DicodingEventAdapter(viewModel, true)
        listRecyclerView.adapter = listAdapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeFavoritesEvents() {
        viewModel.fetchFavoritesEvents().observe(viewLifecycleOwner) { result ->
            listAdapter.setEvents(result)
        }
    }

    override fun onResume() {
        super.onResume()
        observeFavoritesEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}