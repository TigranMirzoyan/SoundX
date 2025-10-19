package com.soundx.util

data class YouTubeResponse(val items: List<YouTubeItem>)

data class YouTubeItem(val id: YouTubeId?, val snippet: YouTubeSnippet)

data class YouTubeId(val videoId: String?)

data class YouTubeSnippet(
    val title: String,
    val channelTitle: String,
)

data class YouTubeSong(
    val videoId: String,
    val title: String,
    val channelTitle: String
)

fun YouTubeItem.toYouTubeSongOrNull(): YouTubeSong? {
    val videoId = this.id?.videoId
    val title = this.snippet.title
    val channelTitle = this.snippet.channelTitle

    return if (!videoId.isNullOrEmpty()) {
        YouTubeSong(
            videoId = videoId,
            title = title,
            channelTitle = channelTitle
        )
    } else null
}