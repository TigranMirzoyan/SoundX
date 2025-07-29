package com.soundx.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.soundx.databinding.FragmentYoutubePlayerBinding
import com.soundx.util.DefaultFragments
import com.soundx.util.NavigationManager
import com.soundx.viewmodel.MusicViewModel

class YoutubePlayer : Fragment() {
    private lateinit var binding: FragmentYoutubePlayerBinding
    private lateinit var youTubePlayer: YouTubePlayer
    private lateinit var viewModel: MusicViewModel
    private var currentVideoIndex = -1
    private var videoIds = mutableListOf<String>()
    private var titles = mutableListOf<String>()
    private var channelTitles = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYoutubePlayerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setUpYoutubePlayer()
        setupBackButtonClick()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
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
        viewModel.searchVideos.observe(viewLifecycleOwner) { videos ->
            if (videos.isEmpty()) return@observe
            videoIds = videos.map { it.videoId }.toMutableList()
            titles = videos.map { it.title }.toMutableList()
            channelTitles = videos.map { it.channelTitle }.toMutableList()
        }
    }

    private fun observeVideoSelection() {
        viewModel.selectedVideoPosition.observe(viewLifecycleOwner) { position ->
            if (position !in videoIds.indices) return@observe
            currentVideoIndex = position

            check(::youTubePlayer.isInitialized) {"YouTubePlayer field is not Initialized "}
            loadCurrentVideo()
        }
    }

    private fun setUpYoutubePlayer() {
        binding.youtubePlayerView.enableBackgroundPlayback(true)
        binding.youtubePlayerView.enableAutomaticInitialization = false

        val options = IFramePlayerOptions.Builder()
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
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
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
                NavigationManager.navigateToFragment(DefaultFragments.SEARCH_FRAGMENT)
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
        val thumbnailUrl =
            "https://img.youtube.com/vi/${videoIds[currentVideoIndex]}/maxresdefault.jpg"

        Glide.with(this)
            .load(thumbnailUrl)
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
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    NavigationManager.navigateToFragment(DefaultFragments.SEARCH_FRAGMENT)
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::binding.isInitialized) {
            binding.youtubePlayerView.release()
        }
    }
}