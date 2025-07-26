package com.soundx.model.youtube

import com.soundx.BuildConfig
import com.soundx.util.YouTubeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = 10,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ) : YouTubeResponse
}