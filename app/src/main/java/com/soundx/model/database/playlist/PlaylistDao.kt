package com.soundx.model.database.playlist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistDao {
    @Insert
    suspend fun add(playlist: Playlist)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAll(): LiveData<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun get(id: Int): Playlist?

    @Query("UPDATE playlists SET imagePath = :imagePath, name = :name, bio = :bio WHERE id = :id")
    suspend fun editPlaylist(id: Int, imagePath: String, name: String, bio: String)
}