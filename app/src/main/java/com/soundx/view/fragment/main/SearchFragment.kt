package com.soundx.view.fragment.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.soundx.R
import com.soundx.databinding.FragmentSearchBinding
import com.soundx.appfragment.DefaultFragment
import com.soundx.util.NavigationManager
import com.soundx.util.SearchHistoryManager
import com.soundx.util.YouTubeSong
import com.soundx.view.adapter.SearchHistoryAdapter
import com.soundx.view.adapter.SearchSongAdapter
import com.soundx.view.fragment.other.SongPlayer
import com.soundx.viewmodel.SongViewModel

class SearchFragment : DefaultFragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SongViewModel
    private lateinit var songAdapter: SearchSongAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val searchHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[SongViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        songAdapter = SearchSongAdapter(setSongOnClickListener())
        historyAdapter = SearchHistoryAdapter (
            onItemClick = { query ->
                binding.searchView.setQuery(query, true)
            },
            onDeleteClick = { query ->
                SearchHistoryManager.deleteQuery(requireContext(), query)
                showSearchHistory()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = historyAdapter

        showSearchHistory()
        viewModel.searchSongs.observe(viewLifecycleOwner) { songs ->
            if (binding.searchView.query.isNullOrEmpty()) {
                showSearchHistory()
            } else {
                showSongs(songs)
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setIconifiedByDefault(false)
        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        searchEditText.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.primary_text_color
            )
        )

        searchEditText.setHintTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.primary_hint_color
            )
        )

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.clearSearchedVideos()
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({
                    viewModel.clearSearchedVideos()
                    if (!newText.isNullOrEmpty()) performSearch(newText)

                }, 256)
                return true
            }
        })
    }

    private fun showSongs(songs: List<YouTubeSong>) {
        binding.recyclerView.adapter = songAdapter
        songAdapter.submitList(songs)
    }

    private fun showSearchHistory() {
        val history = SearchHistoryManager.getQueries(requireContext())
        binding.recyclerView.adapter = historyAdapter
        historyAdapter.submitList(history)
    }

    private fun setSongOnClickListener(): (Int) -> Unit = { position ->
        viewModel.selectSong(position)
        val query = binding.searchView.query.toString()
        if (query.isNotBlank()) {
            SearchHistoryManager.saveQuery(requireContext(), query)
        }

        NavigationManager.navigateToFragment(SongPlayer::class)
    }

    private fun performSearch(query: String) {
        viewModel.searchSongFromYoutube(query)
    }
}