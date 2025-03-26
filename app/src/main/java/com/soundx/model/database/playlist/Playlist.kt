package com.soundx.model.database.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var bio: String? = null,
    var imagePath: String,
    var songsNum: Int
)
