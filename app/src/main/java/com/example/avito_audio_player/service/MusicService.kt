package com.example.avito_audio_player.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import javax.inject.Inject
import com.google.android.exoplayer2.C


@AndroidEntryPoint
class MusicService : Service(), Player.Listener {

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var mediaSession: MediaSessionCompat

    private val binder = MusicServiceBinder()

    // Список треков и текущий индекс
    private var trackList: List<UnifiedTrack> = emptyList()
    private var currentTrackIndex: Int = 0

    // Handler для периодического обновления состояния плеера и уведомления
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updatePlaybackState(if (exoPlayer.isPlaying) PlaybackStateCompat.STATE_PAUSED else PlaybackStateCompat.STATE_PLAYING)
            updateNotification()
            if (exoPlayer.isPlaying) {
                updateHandler.postDelayed(this, 500)
            }
        }
    }

    companion object {
        const val ACTION_PLAY = "com.example.avito_audio_player.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.avito_audio_player.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.avito_audio_player.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.avito_audio_player.ACTION_PREVIOUS"
    }

    inner class MusicServiceBinder : Binder() {
        fun getService() = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer.addListener(this)
        mediaSession.apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    play()
                }
                override fun onPause() {
                    pause()
                }
                override fun onSkipToNext() {
                    playNextTrack()
                }
                override fun onSkipToPrevious() {
                    playPreviousTrack()
                }
                override fun onSeekTo(pos: Long) {
                    seekTo(pos)
                }
            })
            isActive = true
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    // Обработка интентов
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY -> play()
                ACTION_PAUSE -> pause()
                ACTION_NEXT -> playNextTrack()
                ACTION_PREVIOUS -> playPreviousTrack()
            }
        }
        return START_STICKY
    }

    // Передаём список треков и стартовый индекс из фрагмента
    fun setTracks(tracks: List<UnifiedTrack>, startIndex: Int) {
        trackList = tracks
        currentTrackIndex = startIndex
        playCurrentTrack()
    }

    private fun playCurrentTrack() {
        if (trackList.isNotEmpty() && currentTrackIndex in trackList.indices) {
            val currentTrack = trackList[currentTrackIndex]
            val uri = currentTrack.preview
            if (!uri.isNullOrBlank()) {
                exoPlayer.setMediaItem(MediaItem.fromUri(uri))
                exoPlayer.prepare()
                updateMediaSessionMetadata(currentTrack)
                play()
                updateHandler.post(updateRunnable)
            }
        }
    }

    fun play() {
        exoPlayer.play()
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        updateNotification()
        updateHandler.post(updateRunnable)
    }

    fun pause() {
        exoPlayer.pause()
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
        updateNotification()
        updateHandler.removeCallbacks(updateRunnable)
    }

    fun playNextTrack() {
        if (trackList.isNotEmpty()) {
            currentTrackIndex = (currentTrackIndex + 1) % trackList.size
            playCurrentTrack()
            updateNotification()
        }
    }

    fun playPreviousTrack() {
        if (trackList.isNotEmpty()) {
            currentTrackIndex = if (currentTrackIndex - 1 < 0) trackList.size - 1 else currentTrackIndex - 1
            playCurrentTrack()
            updateNotification()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING) // Обновляем состояние
        updateNotification()
    }


    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun getCurrentPosition(): Long = exoPlayer.currentPosition

    fun getDuration(): Long = exoPlayer.duration

    // При окончании трека переходим к следующему
    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
            playNextTrack()
        }
        val currentTrack = trackList[currentTrackIndex]
        updateMediaSessionMetadata(currentTrack)
        updateNotification()
    }

    // Обновляем уведомление через NotificationHelper, передавая текущую позицию и длительность
    fun updateNotification() {
        val notification = notificationHelper.buildNotification(
            track = trackList.getOrNull(currentTrackIndex),
            currentPosition = exoPlayer.currentPosition,
            duration = exoPlayer.duration,
            mediaSessionToken = mediaSession.sessionToken
        )
        startForeground(NotificationHelper.NOTIFICATION_ID, notification)
    }

    // Обновляем метаданные MediaSession (название, артист, обложка)
    private fun updateMediaSessionMetadata(track: UnifiedTrack) {
        val duration = if (exoPlayer.duration != C.TIME_UNSET) exoPlayer.duration else 0L

        val bitmap: Bitmap? = try {
            if (!track.coverUri.isNullOrBlank()) {
                BitmapFactory.decodeStream(URL(track.coverUri).openStream())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            .build()

        mediaSession.setMetadata(metadata)
    }


    // Обновляем состояние воспроизведения в MediaSession
    private fun updatePlaybackState(state: Int) {
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(state, exoPlayer.currentPosition, 1f, System.currentTimeMillis())
            .setBufferedPosition(exoPlayer.bufferedPosition)
            .build()

        mediaSession.setPlaybackState(playbackState)
    }

    override fun onDestroy() {
        updateHandler.removeCallbacks(updateRunnable)
        exoPlayer.release()
        mediaSession.release()
        super.onDestroy()
    }
}