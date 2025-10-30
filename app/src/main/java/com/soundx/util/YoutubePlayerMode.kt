package com.soundx.util

sealed class YoutubePlayerMode {
    data class FromSearch(val song: YouTubeSong): YoutubePlayerMode()
    data object FromLink : YoutubePlayerMode()
    data object None : YoutubePlayerMode()
}