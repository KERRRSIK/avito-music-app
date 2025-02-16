package com.example.avito_audio_player.data.model

data class TrackLocal(
    val id: String,
    val title: String,
    val artist: String,
    val preview: String,
    val duration: Long,
    val coverUri: String
)
