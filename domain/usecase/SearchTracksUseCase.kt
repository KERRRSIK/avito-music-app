package com.example.avito_audio_player.domain.usecase

import com.example.avito_audio_player.data.model.TrackAPI
import com.example.avito_audio_player.data.repository.TrackRepository
import javax.inject.Inject

//UseCase для поиска треков из API
class SearchTracksUseCase @Inject constructor(
    private val tracksRepository: TrackRepository
) {
    suspend fun execute(query: String): List<TrackAPI> {
        return if (query.isEmpty()) {
            tracksRepository.getTopTracks() // Если запрос пустой, возвращаем все треки
        } else {
            tracksRepository.searchTracks(query) // Ищем треки по запросу
        }
    }
}
