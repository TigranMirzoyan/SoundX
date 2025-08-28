package com.soundx.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.youtube.RetrofitYoutube
import com.soundx.util.YouTubeVideo
import com.soundx.util.toYouTubeVideo
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchVideos = MutableLiveData<List<YouTubeVideo>>()
    private val _selectedVideoPosition = MutableLiveData<Int>()
    val searchVideos: LiveData<List<YouTubeVideo>> = _searchVideos
    val selectedVideoPosition: LiveData<Int> = _selectedVideoPosition

    fun searchVideosFromYoutube(query: String) = viewModelScope.launch {
        runCatching {
            RetrofitYoutube.youtubeApi.searchVideos(query = query)
        }.onSuccess { response ->
            _searchVideos.postValue(response.items.map { it.toYouTubeVideo() })
        }.onFailure { e ->
            Log.e("YouTubeAPI", "Error searching videos", e)
        }
    }

    fun clearSearchedVideos() {
        _searchVideos.postValue(emptyList())
    }

    fun selectVideo(position: Int) {
        _selectedVideoPosition.value = position
    }
}