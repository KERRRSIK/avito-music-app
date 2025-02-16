package com.example.avito_audio_player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.example.avito_audio_player.domain.usecase.GetTracksFromApiUseCase
import com.example.avito_audio_player.domain.usecase.SearchTracksUseCase
import com.example.avito_audio_player.utils.toUnifiedTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TracksViewModel @Inject constructor(
    private val getTracksFromApiUseCase: GetTracksFromApiUseCase,
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<UnifiedTrack>>(emptyList())
    val tracks = _tracks.asStateFlow()

    private val _filteredTracks = MutableStateFlow<List<UnifiedTrack>>(emptyList())
    val filteredTracks = _filteredTracks.asStateFlow()

    fun loadTracks() {
        viewModelScope.launch {
            val apiTracks = getTracksFromApiUseCase.execute()
            val unifiedTracks = apiTracks.map { it.toUnifiedTrack() }
            _tracks.value = unifiedTracks
            _filteredTracks.value = unifiedTracks
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            val searchResult = searchTracksUseCase.execute(query)
            _filteredTracks.value = searchResult.map { it.toUnifiedTrack() }
        }
    }
}
