package com.soundx.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.database.AppDatabase
import com.soundx.model.database.music.Music
import com.soundx.model.repository.MusicRepository
import com.soundx.model.youtube.YouTubeVideo
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MusicRepository
    private val _searchedVideos = MutableLiveData<List<YouTubeVideo>>()

    val allMusics: LiveData<List<Music>>
    val searchedVideos: LiveData<List<YouTubeVideo>> get() = _searchedVideos

    init {
        val musicDao = AppDatabase.getDatabase(application).musicDao()
        repository = MusicRepository(musicDao)
        allMusics = repository.allMusics
    }

    fun searchVideos(query: String) = viewModelScope.launch {
        try {
            val videoList = repository.searchVideos(query)
            _searchedVideos.postValue(videoList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearSearchedVideos() {
        _searchedVideos.postValue(emptyList())
    }

    fun add(music: Music) = viewModelScope.launch {
        repository.add(music)
    }
}
