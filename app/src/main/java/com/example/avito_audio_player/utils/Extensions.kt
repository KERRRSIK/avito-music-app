package com.example.avito_audio_player.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.avito_audio_player.R
import com.example.avito_audio_player.data.model.TrackAPI
import com.example.avito_audio_player.data.model.TrackLocal
import com.example.avito_audio_player.data.model.UnifiedTrack

// Функция расширения для ImageView для загрузки изображения из URL или URI
fun ImageView.loadImage(url: String?) {
    // Проверяем, что URL не равен null и не пустой
    if (!url.isNullOrEmpty()) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_music_note) // пока изображение загружается
            .into(this)
    } else {
        // Если URL пустой, то заглушка
        this.setImageResource(R.drawable.ic_music_note)
    }
}

/**
 * Преобразует [TrackAPI]  в [UnifiedTrack] – общую модель трека.
 */
fun TrackAPI.toUnifiedTrack(): UnifiedTrack {
    return UnifiedTrack(
        id = this.id.toString(),
        title = this.title,
        artist = this.artist.name,
        preview = this.preview,
        duration = this.duration*1000L,
        coverUri = this.album.cover
    )
}

/**
 * Преобразует [TrackLocal] в [UnifiedTrack] – общую модель трека.
 */
fun TrackLocal.toUnifiedTrack(): UnifiedTrack {
    return UnifiedTrack(
        id = this.id,
        title = this.title,
        artist = this.artist,
        preview = this.preview,
        duration = this.duration,
        coverUri = this.coverUri
    )
}