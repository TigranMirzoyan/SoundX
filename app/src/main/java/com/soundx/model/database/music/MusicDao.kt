package com.soundx.model.database.music

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MusicDao {
    @Insert
    suspend fun add(music: Music)

    @Query("SELECT * FROM music ORDER BY name ASC")
    fun getAll(): LiveData<List<Music>>
}