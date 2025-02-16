package com.example.avito_audio_player.data.model

data class TrackAPI(
    val id: Long,
    val title: String,
    val artist: Artist,
    val preview: String,
    val duration: Long,
    val album: Album
)