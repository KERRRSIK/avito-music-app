package com.example.avito_audio_player.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.avito_audio_player.data.model.TrackLocal
import javax.inject.Inject

class LocalTrackRepository @Inject constructor(
    private val context: Context
) {
    fun getLocalTracks(): List<TrackLocal> {
        val tracks = mutableListOf<TrackLocal>()

        // Запрос к хранилищу музыки на устройстве
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)


            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val title = it.getString(titleIndex)
                val artist = it.getString(artistIndex)
                val preview = it.getString(dataIndex)
                val duration = it.getLong(durationIndex)
                val albumId = it.getInt(albumIdIndex)
                // URI для обложки альбома
                val coverUri = Uri.parse("content://media/external/audio/albumart/$albumId").toString()

                tracks.add(TrackLocal(id, title, artist, preview, duration, coverUri))
                Log.d("LocalTrackRepository", "Добавлен трек: $title - $preview")            }
        }
        return tracks
    }
}
