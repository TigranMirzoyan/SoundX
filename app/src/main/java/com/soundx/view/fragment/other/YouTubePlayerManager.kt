package com.soundx.view.fragment.other

import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class YouTubePlayerManager(
    private val playerView: YouTubePlayerView,
    private val seekBar: YouTubePlayerSeekBar,
    private val onStateChange: (PlayerConstants.PlayerState) -> Unit
) {
    private var youTubePlayer: YouTubePlayer? = null

    fun initialize() {
        playerView.enableAutomaticInitialization = false

        playerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                this@YouTubePlayerManager.youTubePlayer = youTubePlayer
                setSeekBar()
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                onStateChange(state)
            }
        })
    }

    private fun setSeekBar() {
        youTubePlayer?.addListener(seekBar)
        seekBar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) {
                youTubePlayer?.seekTo(time)
            }
        }
    }

    fun loadVideo(videoID: String, startSeconds: Float = 0f) {
        youTubePlayer?.loadVideo(videoID, startSeconds)
    }

    fun cueVideo(videoID: String, startSeconds: Float = 0f){
        youTubePlayer?.cueVideo(videoID, startSeconds)
    }

    fun pause() {
        youTubePlayer?.pause()
    }

    fun play() {
        youTubePlayer?.play()
    }

    fun release(){
        playerView.release()
    }
}