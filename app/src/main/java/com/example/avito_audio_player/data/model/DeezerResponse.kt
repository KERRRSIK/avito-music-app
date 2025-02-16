package com.example.avito_audio_player.data.model

data class DeezerResponse(
    val `data`: List<TrackAPI>,
    val next: String,
    val total: Int
)