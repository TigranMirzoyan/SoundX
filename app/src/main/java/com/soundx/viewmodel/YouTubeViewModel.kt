package com.soundx.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.database.AppDatabase
import com.soundx.model.database.music.Music
import com.soundx.model.repository.YouTubeRepository
import com.soundx.model.youtube.YouTubeVideo
import kotlinx.coroutines.launch

class YouTubeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: YouTubeRepository
    private val _searchVideos = MutableLiveData<List<YouTubeVideo>>()
    val searchVideos: LiveData<List<YouTubeVideo>> get() = _searchVideos
    val allMusics: LiveData<List<Music>>

    init {
        val musicDao = AppDatabase.getDatabase(application).musicDao()
        repository = YouTubeRepository(musicDao)
        allMusics = repository.allMusics
    }

    fun searchVideos(query: String) = viewModelScope.launch {
        try {
            val videoList = repository.searchVideos(query)
            _searchVideos.postValue(videoList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun add(music: Music) = viewModelScope.launch {
        repository.add(music)
    }
}
