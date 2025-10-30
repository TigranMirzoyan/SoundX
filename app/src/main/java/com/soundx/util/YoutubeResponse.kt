package com.soundx.util

data class YouTubeResponse<T>(val items: List<T>)

data class SearchYouTubeItem(val id: SearchYouTubeId?, val snippet: YouTubeSnippet)

data class VideoYouTubeItem(val id: String, val snippet: YouTubeSnippet)

data class SearchYouTubeId(val videoId: String?)

data class YouTubeSnippet(
    val title: String,
    val channelTitle: String,
)

data class YouTubeSong(
    val videoId: String, val title: String, val channelTitle: String
)

fun SearchYouTubeItem.toYouTubeSongOrNull(): YouTubeSong? {
    val videoId = this.id?.videoId
    val title = this.snippet.title
    val channelTitle = this.snippet.channelTitle

    return if (!videoId.isNullOrEmpty()) {
        YouTubeSong(videoId = videoId, title = title, channelTitle = channelTitle)
    } else null
}

fun VideoYouTubeItem.toYouTubeSong(): YouTubeSong =
    YouTubeSong(id, snippet.title, snippet.channelTitle)