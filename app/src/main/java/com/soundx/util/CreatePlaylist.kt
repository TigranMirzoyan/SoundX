package com.soundx.util

import com.soundx.model.database.playlist.Playlist

object CreatePlaylist {
    fun createPlaylist(name: String, bio: String, imagePath: String): Playlist {
        val playlist = Playlist(
            name = name,
            bio = bio,
            imagePath = imagePath,
            songsNum = 0
        )
        return playlist
    }
}