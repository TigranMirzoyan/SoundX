package com.soundx.model.youtube

import com.soundx.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = 15,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("videoCategoryId") videoCategoryId: String = "10",
        @Query("key") apiKey: String = BuildConfig.apiKey
    ): YouTubeResponse
}
