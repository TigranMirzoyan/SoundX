package com.soundx.view.fragment.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.soundx.databinding.FragmentSearchBinding
import com.soundx.view.adapter.SearchAdapter
import com.soundx.viewmodel.MusicViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: MusicViewModel
    private val searchHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        setupSearchView()
    }

    private fun configureRecyclerView() {
        val adapter = SearchAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.searchedVideos.observe(viewLifecycleOwner) { videos ->
            adapter.submitList(videos)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({
                    if (newText.isNullOrEmpty()) {
                        viewModel.clearSearchedVideos()
                    } else {
                        performSearch(newText)
                    }
                }, 200)
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        if (query.isNotBlank()) {
            viewModel.searchVideos(query)
        }
    }
}