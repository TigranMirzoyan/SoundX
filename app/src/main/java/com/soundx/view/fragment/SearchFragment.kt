package com.soundx.view.fragment

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
import com.soundx.util.YouTubeSong
import com.soundx.view.adapter.SearchMusicAdapter
import com.soundx.viewmodel.MusicViewModel

class SearchFragment : DefaultFragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: MusicViewModel
    private val searchHandler = Handler(Looper.getMainLooper())
    private var currentSongs: List<YouTubeSong> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        val adapter = SearchMusicAdapter(setItemOnClickListener())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.searchSongs.observe(viewLifecycleOwner) { songs ->
            binding.searchingSongs.visibility = if (!songs.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(songs)
            currentSongs = songs
        }
    }

    private fun setupSearchView() {
        binding.searchView.setIconifiedByDefault(false)
        val searchEditText =
            binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        searchEditText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_text_color
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

    private fun setItemOnClickListener(): (Int) -> Unit = { position ->
        viewModel.selectVideo(position)
        NavigationManager.navigateToFragment(YoutubePlayer::class)
    }

    private fun performSearch(query: String) {
        viewModel.searchMusicFromYoutube(query)
    }
}