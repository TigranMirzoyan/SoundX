package com.soundx.model.youtube

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitYoutube {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    val youtubeApi: YoutubeApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(YoutubeApi::class.java)
}