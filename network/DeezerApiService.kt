package com.example.avito_audio_player.data.network

import com.example.avito_audio_player.data.model.Chart
import com.example.avito_audio_player.data.model.DeezerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApiService {
    @GET("chart")
    suspend fun getTopTracks(): Response<Chart>

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): Response<DeezerResponse>
}
