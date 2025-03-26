package com.soundx.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.soundx.model.database.playlist.Playlist
import com.soundx.model.database.AppDatabase
import com.soundx.model.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlaylistRepository
    val allPlaylists: LiveData<List<Playlist>>

    init {
        val playlistDao = AppDatabase.getDatabase(application).playlistDao()
        repository = PlaylistRepository(playlistDao)
        allPlaylists = repository.allPlaylists
    }

    fun add(playlist: Playlist) = viewModelScope.launch {
        repository.add(playlist)
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }

    fun get(id: Int, callback: (Playlist?) -> Unit) {
        viewModelScope.launch {
            val playlist = repository.get(id)
            callback(playlist)
        }
    }

    fun editPlaylist(id: Int, imagePath: String, name: String, bio: String) = viewModelScope.launch {
        repository.editPlaylist(id, imagePath, name, bio)
    }
}