package com.soundx.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.soundx.R
import com.soundx.databinding.FragmentYoutubePlayerBinding
import com.soundx.util.NavigationManager
import com.soundx.appfragment.SpecialFragment
import com.soundx.viewmodel.MusicViewModel

class YoutubePlayer : SpecialFragment() {
    private lateinit var binding: FragmentYoutubePlayerBinding
    private lateinit var youTubePlayer: YouTubePlayer
    private lateinit var viewModel: MusicViewModel
    private var currentVideoIndex = -1
    private var videoIds = mutableListOf<String>()
    private var titles = mutableListOf<String>()
    private var channelTitles = mutableListOf<String>()
    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYoutubePlayerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpYoutubePlayer()
        observeViewModel()
        setupBackButtonClick()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            loadCurrentVideo()
            backPressedCallback?.isEnabled = true
        } else {
            backPressedCallback?.isEnabled = false
        }

        if (hidden && ::youTubePlayer.isInitialized) {
            currentVideoIndex = -1
            youTubePlayer.pause()
        }
    }

    private fun observeViewModel() {
        observeSearchVideos()
        observeVideoSelection()
    }

    private fun observeSearchVideos() {
        viewModel.searchSongs.observe(viewLifecycleOwner) { videos ->
            if (videos.isEmpty()) return@observe
            videoIds = videos.map { it.videoId }.toMutableList()
            titles = videos.map { it.title }.toMutableList()
            channelTitles = videos.map { it.channelTitle }.toMutableList()
        }
    }

    private fun observeVideoSelection() {
        viewModel.selectedSongPosition.observe(viewLifecycleOwner) { position ->
            if (position !in videoIds.indices) return@observe

            currentVideoIndex = position
        }
    }

    private fun setUpYoutubePlayer() {
        binding.youtubePlayerView.enableAutomaticInitialization = false

        val options = IFramePlayerOptions.Builder(requireContext())
            .controls(0)
            .rel(0)
            .ivLoadPolicy(3)
            .build()

        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@YoutubePlayer.youTubePlayer = youTubePlayer
                setupCustomControls()
                setupSeekBar()
                loadCurrentVideo()
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState
            ) {
                updateUI(state)
            }
        }, options)
    }

    private fun setupSeekBar() {
        youTubePlayer.addListener(binding.youtubePlayerSeekbar)

        binding.youtubePlayerSeekbar.youtubePlayerSeekBarListener =
            object : YouTubePlayerSeekBarListener {
                override fun seekTo(time: Float) {
                    youTubePlayer.seekTo(time)
                }
            }
    }

    private fun setupCustomControls() {
        with(binding) {
            playVideoBtn.setOnClickListener {
                youTubePlayer.play()
            }

            pauseVideoBtn.setOnClickListener {
                youTubePlayer.pause()
            }

            previousVideoBtn.setOnClickListener {
                if (currentVideoIndex <= 0) return@setOnClickListener

                currentVideoIndex--
                loadCurrentVideo()
            }

            minimize.setOnClickListener {
                NavigationManager.navigateToFragment(SearchFragment::class)
            }

            nextVideoBtn.setOnClickListener {
                if (currentVideoIndex >= videoIds.size - 1) return@setOnClickListener
                currentVideoIndex++
                loadCurrentVideo()
            }
        }

        updateNavigationButtons()
    }

    private fun updateUI(state: PlayerConstants.PlayerState) {
        when (state) {
            PlayerConstants.PlayerState.PLAYING -> {
                binding.playVideoBtn.visibility = View.GONE
                binding.pauseVideoBtn.visibility = View.VISIBLE
            }

            PlayerConstants.PlayerState.PAUSED -> {
                binding.playVideoBtn.visibility = View.VISIBLE
                binding.pauseVideoBtn.visibility = View.GONE
            }

            PlayerConstants.PlayerState.ENDED -> {
                binding.playVideoBtn.visibility = View.VISIBLE
                binding.pauseVideoBtn.visibility = View.GONE

                if (currentVideoIndex < videoIds.size - 1) {
                    currentVideoIndex++
                    loadCurrentVideo()
                }
            }

            else -> {}
        }
    }

    private fun loadCurrentVideo() {
        if (currentVideoIndex >= videoIds.size || currentVideoIndex < 0) return
        check(::youTubePlayer.isInitialized) { "YouTubePlayer field is not Initialized " }

        val videoId = videoIds[currentVideoIndex]

        youTubePlayer.loadVideo(videoId, 0f)
        updateVideoInfo()
        updateVideoThumbnail()
        updateNavigationButtons()
    }

    private fun updateVideoInfo() {
        binding.title.text = titles[currentVideoIndex]
        binding.channelTitle.text = channelTitles[currentVideoIndex]

        binding.title.isSelected = true
        binding.channelTitle.isSelected = true
    }

    private fun updateVideoThumbnail() {
        val videoId = videoIds[currentVideoIndex]
        val maxResThumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
        val hqThumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

        Glide.with(this)
            .load(maxResThumbnailUrl)
            .placeholder(R.drawable.ic_music_default)
                .error(Glide.with(this)
                    .load(hqThumbnailUrl)
                    .error(R.drawable.ic_music_default)
                    .centerCrop())
            .centerCrop()
            .into(binding.thumbnail)
    }

    private fun updateNavigationButtons() {
        with(binding) {
            previousVideoBtn.isEnabled = currentVideoIndex > 0
            nextVideoBtn.isEnabled = currentVideoIndex < videoIds.size - 1
            previousVideoBtn.alpha = if (currentVideoIndex > 0) 1.0f else 0.5f
            nextVideoBtn.alpha = if (currentVideoIndex < videoIds.size - 1) 1.0f else 0.5f
        }
    }

    private fun setupBackButtonClick() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavigationManager.navigateToFragment(SearchFragment::class)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, backPressedCallback!!
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!::binding.isInitialized) return
        binding.youtubePlayerView.release()
    }
}