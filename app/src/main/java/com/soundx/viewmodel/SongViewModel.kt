package com.soundx.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.youtube.RetrofitYoutube
import com.soundx.util.YouTubeSong
import com.soundx.util.YoutubePlayerMode
import com.soundx.util.toYouTubeSong
import com.soundx.util.toYouTubeSongOrNull
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchSongs = MutableLiveData<List<YouTubeSong>>()
    private val _youTubePlayerMode = MutableLiveData<YoutubePlayerMode>(YoutubePlayerMode.None)
    val searchSongs: LiveData<List<YouTubeSong>> = _searchSongs
    val youTubePlayerMode: LiveData<YoutubePlayerMode> = _youTubePlayerMode

    fun searchSongFromYoutube(query: String) {
        viewModelScope.launch {
            runCatching {
                RetrofitYoutube.youtubeApi.searchVideos(query = query)
            }.onSuccess { response ->
                _searchSongs.postValue(response.items.mapNotNull { it.toYouTubeSongOrNull() })
            }.onFailure { e ->
                Log.e("YouTubeAPI", "Error searching videos", e)
            }
        }
    }

    fun getYouTubeVideoInfo(videoId: String, callback: (YouTubeSong?) -> Unit) {
        viewModelScope.launch {
            runCatching {
                RetrofitYoutube.youtubeApi.getVideoById(videoId = videoId)
            }.onSuccess { response ->
                val song = response.items.firstOrNull()?.toYouTubeSong()
                callback(song)
            }.onFailure { e ->
                Log.e("YouTubeAPI", "Error fetching video info", e)
                callback(null)
            }
        }
    }

    fun clearSearchedVideos() {
        _searchSongs.postValue(emptyList())
        _youTubePlayerMode.value = YoutubePlayerMode.None
    }

    fun selectSong(position: Int) {
        val song = _searchSongs.value?.getOrNull(position) ?: return
        _youTubePlayerMode.value = YoutubePlayerMode.FromSearch(song)
    }

    fun selectLinkMode() {
        _youTubePlayerMode.value = YoutubePlayerMode.FromLink
    }

    fun selectNoneMode(){
        _youTubePlayerMode.value =  YoutubePlayerMode.None
    }

    fun nextSong() {
        val position = findSongPosition()
        val list = _searchSongs.value ?: return
        if (position in 0 until list.size - 1) selectSong(position + 1)
    }

    fun previousSong() {
        val position = findSongPosition()
        if (position > 0) selectSong(position - 1)
    }

    fun findSongPosition(): Int {
        val currentVideo = (_youTubePlayerMode.value as? YoutubePlayerMode.FromSearch)?.song
        val list = _searchSongs.value ?: return -1
        val currentPosition = list.indexOf(currentVideo)
        return currentPosition
    }
}