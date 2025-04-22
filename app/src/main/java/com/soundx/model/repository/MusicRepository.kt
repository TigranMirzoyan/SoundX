package com.soundx.model.repository

import androidx.lifecycle.LiveData
import com.soundx.model.database.music.Music
import com.soundx.model.database.music.MusicDao
import com.soundx.model.youtube.RetrofitClient.youtubeApi
import com.soundx.model.youtube.YouTubeVideo

class MusicRepository(private val musicDao: MusicDao) {
    val allMusics: LiveData<List<Music>> = musicDao.getAll()

    suspend fun searchVideos(query: String): List<YouTubeVideo> {
        val response = youtubeApi.searchVideos(query = query)
        return response.items.map {
            YouTubeVideo(
                it.id.videoId,
                it.snippet.title,
                it.snippet.channelTitle,
                it.snippet.thumbnails.medium.url
            )
        }
    }

    suspend fun add(music: Music) {
        musicDao.add(music)
    }
}