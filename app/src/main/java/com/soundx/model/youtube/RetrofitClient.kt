package com.soundx.model.youtube

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val youtubeApi: YouTubeApi = retrofit.create(YouTubeApi::class.java)
}