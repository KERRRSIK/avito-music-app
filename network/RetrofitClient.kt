package com.example.avito_audio_player.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://api.deezer.com/"

    val api: DeezerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // конвертер JSON в объекты
            .build()
            .create(DeezerApiService::class.java)
    }
}