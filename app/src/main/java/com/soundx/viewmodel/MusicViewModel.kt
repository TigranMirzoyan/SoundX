package com.soundx.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.youtube.RetrofitYoutube
import com.soundx.util.YouTubeVideo
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchVideos = MutableLiveData<List<YouTubeVideo>>()
    private val _selectedVideoPosition = MutableLiveData<Int>()
    val searchVideos: LiveData<List<YouTubeVideo>> get() = _searchVideos
    val selectedVideoPosition: LiveData<Int> get() = _selectedVideoPosition

    fun searchVideosFromYoutube(query: String) = viewModelScope.launch {
        try {
            val response = RetrofitYoutube.youtubeApi.searchVideos(query = query)
            _searchVideos.postValue(response.items.map {
                YouTubeVideo(
                    it.id.videoId,
                    it.snippet.title,
                    it.snippet.channelTitle,
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("YouTubeAPI", "Error: ${e.message}", e)
        }
    }

    fun clearSearchedVideos() {
        _searchVideos.postValue(emptyList())
    }

    fun selectVideo(position: Int) {
        _selectedVideoPosition.value = position
    }
}