package com.soundx.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.soundx.model.database.music.MusicDao
import com.soundx.model.database.playlist.Playlist
import com.soundx.model.database.playlist.PlaylistDao

@Database(entities = [Playlist::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (instance != null) return instance!!

            synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playlist database"
                ).build()
                return instance!!
            }
        }
    }
}