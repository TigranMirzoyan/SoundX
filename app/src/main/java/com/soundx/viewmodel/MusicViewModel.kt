package com.soundx.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.youtube.RetrofitYoutube
import com.soundx.util.YouTubeSong
import com.soundx.util.toYouTubeSong
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchSongs = MutableLiveData<List<YouTubeSong>>()
    private val _selectedSongPosition = MutableLiveData<Int>()
    val searchSongs: LiveData<List<YouTubeSong>> = _searchSongs
    val selectedSongPosition: LiveData<Int> = _selectedSongPosition

    fun searchMusicFromYoutube(query: String) = viewModelScope.launch {
        runCatching {
            RetrofitYoutube.youtubeApi.searchVideos(query = query)
        }.onSuccess { response ->
            _searchSongs.postValue(response.items.map { it.toYouTubeSong() })
        }.onFailure { e ->
            Log.e("YouTubeAPI", "Error searching videos", e)
        }
    }

    fun clearSearchedVideos() {
        _searchSongs.postValue(emptyList())
    }

    fun selectVideo(position: Int) {
        _selectedSongPosition.value = position
    }
}