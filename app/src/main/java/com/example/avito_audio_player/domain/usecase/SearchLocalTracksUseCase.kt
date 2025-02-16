package com.example.avito_audio_player.domain.usecase

import com.example.avito_audio_player.data.model.TrackLocal
import com.example.avito_audio_player.data.repository.LocalTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

//UseCase для поиска локальных треков
class SearchLocalTracksUseCase @Inject constructor(
    private val localTrackRepository: LocalTrackRepository
) {
    fun execute(query: String): Flow<List<TrackLocal>> = flow {
        val allTracks = localTrackRepository.getLocalTracks()
        val filteredTracks = allTracks.filter { track ->
            track.title.contains(query, ignoreCase = true) ||
                    track.artist.contains(query, ignoreCase = true)
        }
        emit(filteredTracks)
    }
}
