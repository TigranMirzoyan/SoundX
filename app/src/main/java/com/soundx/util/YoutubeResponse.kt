package com.soundx.util

data class YouTubeResponse(val items: List<YouTubeItem>)

data class YouTubeItem(val id: YouTubeId, val snippet: YouTubeSnippet)

data class YouTubeId(val videoId: String)

data class YouTubeSnippet(
    val title: String,
    val channelTitle: String,
    val thumbnails: YouTubeThumbnails
)

data class YouTubeThumbnails(val medium: YouTubeThumbnail)

data class YouTubeThumbnail(val url: String)

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val channelTitle: String,
    val thumbnail: String
)
