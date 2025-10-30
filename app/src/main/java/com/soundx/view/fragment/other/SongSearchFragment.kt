package com.soundx.view.fragment.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.soundx.R
import com.soundx.appfragment.SpecialFragment
import com.soundx.databinding.FragmentSongSearchBinding
import com.soundx.thumbnail.ThumbnailQuality
import com.soundx.util.NavigationManager
import com.soundx.util.YouTubeSong
import com.soundx.thumbnail.YouTubeThumbnailLoader
import com.soundx.util.YoutubePlayerMode
import com.soundx.view.fragment.main.SearchFragment
import com.soundx.viewmodel.SongViewModel

class SongSearchFragment : SpecialFragment() {
    private lateinit var binding: FragmentSongSearchBinding
    private lateinit var viewModel: SongViewModel
    private lateinit var playerManager: YouTubePlayerManager
    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[SongViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlayer()
        setupObservers()
        setupCustomControls()
        setupBackButtonClick()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) backPressedCallback?.isEnabled = true
        if (hidden) viewModel.selectNoneMode()
    }

    private fun setupPlayer() {
        playerManager =
            YouTubePlayerManager(binding.youtubePlayerView, binding.youtubePlayerSeekbar) {
                updateUI(it)
            }
        playerManager.initialize()

        binding.title.isSelected = true
        binding.channelTitle.isSelected = true
    }

    private fun setupObservers() {
        viewModel.youTubePlayerMode.observe(viewLifecycleOwner) { mode ->
            setLinkSearchLayoutVisibility()
            when (mode) {
                is YoutubePlayerMode.FromSearch -> loadSong(mode.song)
                YoutubePlayerMode.FromLink -> loadLinkSong()
                YoutubePlayerMode.None -> setDefaultPage()
            }
        }
    }

    private fun loadSong(song: YouTubeSong) {
        playerManager.loadVideo(song.videoId)

        binding.title.text = song.title
        binding.channelTitle.text = song.channelTitle

        YouTubeThumbnailLoader.loadThumbnail(
            song.videoId,
            listOf(ThumbnailQuality.MAX, ThumbnailQuality.HIGH),
            binding.thumbnail,
            this
        )
        updateNavigationButtons()
    }

    private fun loadLinkSong() {
        val input = binding.videoID.text.toString().trim()
        if (input.isEmpty()) return

        val videoID = extractVideoId(input) ?: run {
            Toast.makeText(requireContext(), "Wrong Link", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.getYouTubeVideoInfo(videoID) { song ->
            song?.let {
                playerManager.loadVideo(song.videoId)

                binding.title.text = song.title
                binding.channelTitle.text = song.channelTitle

                YouTubeThumbnailLoader.loadThumbnail(
                    song.videoId,
                    listOf(ThumbnailQuality.MAX, ThumbnailQuality.HIGH),
                    binding.thumbnail,
                    this
                )
            } ?: run {
                Toast.makeText(requireContext(), "Song wasn't found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupCustomControls() {
        with(binding) {
            playVideoBtn.setOnClickListener {
                playerManager.play()
            }

            pauseVideoBtn.setOnClickListener {
                playerManager.pause()
            }

            previousVideoBtn.setOnClickListener {
                viewModel.previousSong()
            }

            nextVideoBtn.setOnClickListener {
                viewModel.nextSong()
            }

            minimize.setOnClickListener {
                NavigationManager.navigateToFragment(SearchFragment::class)
            }

            confirmSong.setOnClickListener {
                loadLinkSong()
            }
        }
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
                viewModel.nextSong()
            }

            else -> {}
        }
    }

    private fun updateNavigationButtons() {
        val position = viewModel.findSongPosition()
        val size = viewModel.searchSongs.value?.size ?: 0

        with(binding) {
            previousVideoBtn.isEnabled = position > 0
            nextVideoBtn.isEnabled = position in 0 until size - 1
            previousVideoBtn.alpha = if (position > 0) 1.0f else 0.5f
            nextVideoBtn.alpha = if (position in 0 until size - 1) 1.0f else 0.5f
        }
    }

    private fun extractVideoId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.matches("^[a-zA-Z0-9_-]{11}$".toRegex())) return trimmed

        val patterns = listOf(
            "v=([a-zA-Z0-9_-]{11})",
            "youtu\\.be/([a-zA-Z0-9_-]{11})",
        )

        for (pattern in patterns) {
            val regex = pattern.toRegex()
            regex.find(trimmed)?.groups?.get(1)?.value?.let { return it }
        }

        return null
    }


    private fun setDefaultPage() {
        binding.videoID.setText("")
        binding.title.text = getString(R.string.fragment_song_player_video_title)
        binding.channelTitle.text = getString(R.string.fragment_song_player_video_channel_title)
        binding.thumbnail.setImageResource(R.drawable.ic_song_default)

        playerManager.pause()
        playerManager.cueVideo("")
        updateNavigationButtons()
    }

    private fun setLinkSearchLayoutVisibility() {
        if (viewModel.youTubePlayerMode.value == YoutubePlayerMode.FromLink) {
            binding.linkSearchLayout.visibility = View.VISIBLE
            return
        }

        binding.linkSearchLayout.visibility = View.GONE
    }

    private fun setupBackButtonClick() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavigationManager.Companion.navigateToFragment(SearchFragment::class)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, backPressedCallback!!
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!::binding.isInitialized) return
        playerManager.release()
    }
}