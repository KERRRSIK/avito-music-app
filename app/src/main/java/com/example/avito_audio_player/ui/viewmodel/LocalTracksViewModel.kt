package com.example.avito_audio_player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_audio_player.data.model.TrackLocal
import com.example.avito_audio_player.domain.usecase.GetLocalTracksUseCase
import com.example.avito_audio_player.domain.usecase.SearchLocalTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalTracksViewModel @Inject constructor(
    private val getLocalTracksUseCase: GetLocalTracksUseCase,
    private val searchLocalTracksUseCase: SearchLocalTracksUseCase
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<TrackLocal>>(emptyList())
    val tracks: StateFlow<List<TrackLocal>> = _tracks.asStateFlow()

    private val _filteredTracks = MutableStateFlow<List<TrackLocal>>(emptyList())
    val filteredTracks: StateFlow<List<TrackLocal>> = _filteredTracks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadLocalTracks()
        // Реактивный поиск при изменении запроса
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isEmpty()) {
                    flowOf(_tracks.value)
                } else {
                    searchLocalTracksUseCase.execute(query)
                }
            }
            .onEach { result ->
                _filteredTracks.value = result
            }
            .launchIn(viewModelScope)
    }

    fun loadLocalTracks() {
        viewModelScope.launch {
            val localTracks = getLocalTracksUseCase.execute()
            _tracks.value = localTracks
            _filteredTracks.value = localTracks
        }
    }

    fun searchTracks(query: String) {
        _searchQuery.value = query
    }
}
