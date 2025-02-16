package com.example.avito_audio_player.data.repository

import android.util.Log
import com.example.avito_audio_player.data.model.TrackAPI
import com.example.avito_audio_player.data.network.DeezerApiService
import javax.inject.Inject

class TrackRepository @Inject constructor(
    private val api: DeezerApiService
) {
    // Получаем топ-треки
    suspend fun getTopTracks(): List<TrackAPI> {
        return try {
            val response = api.getTopTracks()
            if (response.isSuccessful) {
                response.body()?.tracks?.data ?: emptyList()
            } else {
                Log.e("API_ERROR", "Ошибка загрузки топ-треков: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Ошибка сети: ${e.message}")
            emptyList()
        }
    }

    // Поиск треков по запросу
    suspend fun searchTracks(query: String): List<TrackAPI> {
        return try {
            val response = api.searchTracks(query)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                Log.e("API_ERROR", "Ошибка поиска: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Ошибка сети: ${e.message}")
            emptyList()
        }
    }
}
