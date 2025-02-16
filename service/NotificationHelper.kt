package com.example.avito_audio_player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.avito_audio_player.R
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.google.android.exoplayer2.ExoPlayer
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.util.Log

class NotificationHelper(
    private val context: Context,
    private val player: ExoPlayer
) {

    companion object {
        const val CHANNEL_ID = "music_channel"
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    //Собираем уведомление с информацией о треке, действиями и прогрессбаром

    fun buildNotification(
        track: UnifiedTrack?,
        currentPosition: Long,
        duration: Long,
        mediaSessionToken: MediaSessionCompat.Token
    ): Notification {
        val title = track?.title ?: "Неизвестный трек"
        val artist = track?.artist ?: "Неизвестный артист"
        var largeIcon: Bitmap? = null
        try {
            if (!track?.coverUri.isNullOrBlank()) {
                largeIcon = android.graphics.BitmapFactory.decodeStream(
                    java.net.URL(track!!.coverUri).openStream()
                )
            }
        } catch (e: Exception) {
            // Если не удалось загрузить, largeIcon останется null
            Log.e("Notification", "Ошибка загрузки обложки: ${e.message}")
        }

        val prevIntent = getPendingIntent(MusicService.ACTION_PREVIOUS)
        val playPauseAction = if (player.isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_play, "Play", getPendingIntent(MusicService.ACTION_PLAY)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_pause, "Pause", getPendingIntent(MusicService.ACTION_PAUSE)
            )
        }
        val nextIntent = getPendingIntent(MusicService.ACTION_NEXT)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(largeIcon)
            .addAction(NotificationCompat.Action(R.drawable.ic_previous, "Previous", prevIntent))
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent))
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setProgress(duration.toInt(), currentPosition.toInt(), false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}