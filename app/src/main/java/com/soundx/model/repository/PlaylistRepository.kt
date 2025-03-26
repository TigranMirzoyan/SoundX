package com.soundx.model.repository

import androidx.lifecycle.LiveData
import com.soundx.model.database.playlist.Playlist
import com.soundx.model.database.playlist.PlaylistDao

class PlaylistRepository(private val playlistDao: PlaylistDao) {
    val allPlaylists: LiveData<List<Playlist>> = playlistDao.getAll()

    suspend fun add(playlist: Playlist) {
        playlistDao.add(playlist)
    }

    suspend fun delete(id: Int) {
        playlistDao.delete(id)
    }

    suspend fun get(id: Int): Playlist? {
        return playlistDao.get(id)
    }

    suspend fun editPlaylist(id: Int, imagePath: String, name: String, bio: String) {
        playlistDao.editPlaylist(id, imagePath, name, bio)
    }
}