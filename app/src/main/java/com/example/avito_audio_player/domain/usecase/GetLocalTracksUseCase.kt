package com.example.avito_audio_player.domain.usecase

import com.example.avito_audio_player.data.model.TrackLocal
import com.example.avito_audio_player.data.repository.LocalTrackRepository
import javax.inject.Inject

// UseCase для получения списка локальных треков
class GetLocalTracksUseCase @Inject constructor(
    private val localRepository: LocalTrackRepository
) {
    fun execute(): List<TrackLocal> {
        return localRepository.getLocalTracks()
    }
}
