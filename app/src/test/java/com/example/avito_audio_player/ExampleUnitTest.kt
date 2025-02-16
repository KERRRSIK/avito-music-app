package com.example.avito_audio_player

import android.app.Notification
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.example.avito_audio_player.service.NotificationHelper
import com.google.android.exoplayer2.ExoPlayer
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class NotificationHelperTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var mediaSessionToken: MediaSessionCompat.Token

    @Mock
    private lateinit var exoPlayer: ExoPlayer

    @Before
    fun setUp() {
        // Инициализация моков
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test buildNotification with valid track`() {
        val notificationHelper = NotificationHelper(context, exoPlayer)
        val track = UnifiedTrack(id = "42", title = "Track Title", artist = "Artist", preview = "test", duration = 4214L, coverUri = "http://example.com/cover.jpg")

        // Мокируем ExoPlayer (например, его состояние, если нужно)
        Mockito.`when`(exoPlayer.isPlaying).thenReturn(true)

        val notification = notificationHelper.buildNotification(track, 30_000L, 300_000L, mediaSessionToken)

        // Проверяем, что Notification не null
        assertNotNull(notification)

        // Проверяем, что в notification есть ожидаемые параметры
        assertEquals("Track Title", notification.extras.getCharSequence(Notification.EXTRA_TITLE))
        assertEquals("Artist", notification.extras.getCharSequence(Notification.EXTRA_TEXT))
    }

    @Test
    fun `test buildNotification with invalid cover URL`() {
        val notificationHelper = NotificationHelper(context, exoPlayer)
        val track = UnifiedTrack(id = "42", title = "Track Title", artist = "Artist", duration = 1243421L, preview = "Preview", coverUri = "") // Пустой URI
        // Мокируем ExoPlayer
        Mockito.`when`(exoPlayer.isPlaying).thenReturn(false)

        val notification = notificationHelper.buildNotification(track, 30_000L, 300_000L, mediaSessionToken)

        // Проверяем, что Notification не null
        assertNotNull(notification)

        // Проверяем, что в notification нет обложки
        assertNull(notification.largeIcon)
    }
}
