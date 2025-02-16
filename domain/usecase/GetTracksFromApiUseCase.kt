package com.example.avito_audio_player.domain.usecase

import com.example.avito_audio_player.data.model.TrackAPI
import com.example.avito_audio_player.data.repository.TrackRepository
import javax.inject.Inject

//UseCase для получения треков из чарта
class GetTracksFromApiUseCase  @Inject constructor(
    private val trackRepository: TrackRepository
) {
    suspend fun execute(): List<TrackAPI> {
        return trackRepository.getTopTracks()
    }
}
