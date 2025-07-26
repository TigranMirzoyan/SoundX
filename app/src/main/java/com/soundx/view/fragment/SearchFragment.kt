package com.soundx.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.soundx.R
import com.soundx.databinding.FragmentSearchBinding
import com.soundx.util.NavigationManager
import com.soundx.util.SpecialFragments
import com.soundx.util.YouTubeVideo
import com.soundx.view.adapter.SearchVideosAdapter
import com.soundx.viewmodel.MusicViewModel


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: MusicViewModel
    private val searchHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        configureSearchView()
    }

    private fun configureRecyclerView() {
        val adapter = SearchVideosAdapter(setItemOnClickListener())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.searchVideos.observe(viewLifecycleOwner) { videos ->
            adapter.submitList(videos)
            videos.forEach {
                Log.d(
                    "SearchResult",
                    "title = ${it.title}, creator = ${it.channelTitle}"
                )
            }
        }
    }

    private fun configureSearchView() {
        binding.searchView.setIconifiedByDefault(false)
        val searchEditText = binding.searchView
            .findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        searchEditText
            .setHintTextColor(ContextCompat.getColor(requireContext(), R.color.brown_light))

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

    private fun setItemOnClickListener(): (YouTubeVideo) -> Unit = { video ->
        val bundle = Bundle().apply {
            putString("videoId", video.videoId)
            putString("thumbnail", video.thumbnail)
        }

        NavigationManager.navigateToFragment(SpecialFragments.VIEW_MUSIC_VIDEO_FRAGMENT, bundle)
    }

    private fun performSearch(query: String) {
        viewModel.searchVideosFromYoutube(query)
    }
}