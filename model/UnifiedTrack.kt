package com.example.avito_audio_player.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnifiedTrack(
    val id: String,
    val title: String,
    val artist: String,
    val preview: String,
    val duration: Long,
    val coverUri: String
) : Parcelable
