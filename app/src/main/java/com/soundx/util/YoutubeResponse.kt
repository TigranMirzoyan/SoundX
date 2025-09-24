package com.soundx.util

data class YouTubeResponse(val items: List<YouTubeItem>)

data class YouTubeItem(val id: YouTubeId, val snippet: YouTubeSnippet)

data class YouTubeId(val videoId: String)

data class YouTubeSnippet(
    val title: String,
    val channelTitle: String,
)

data class YouTubeSong(
    val videoId: String,
    val title: String,
    val channelTitle: String
)

fun YouTubeItem.toYouTubeSong() = YouTubeSong(
    videoId = this.id.videoId,
    title = this.snippet.title,
    channelTitle = this.snippet.channelTitle
)