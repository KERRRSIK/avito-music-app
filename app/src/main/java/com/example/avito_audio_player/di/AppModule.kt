package com.example.avito_audio_player.di

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.example.avito_audio_player.data.network.DeezerApiService
import com.example.avito_audio_player.data.network.RetrofitClient
import com.example.avito_audio_player.data.repository.LocalTrackRepository
import com.example.avito_audio_player.data.repository.TrackRepository
import com.example.avito_audio_player.service.NotificationHelper
import com.google.android.exoplayer2.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDeezerApiService(): DeezerApiService = RetrofitClient.api

    @Provides
    @Singleton
    fun provideTrackRepository(api: DeezerApiService): TrackRepository =
        TrackRepository(api)

    @Provides
    @Singleton
    fun provideLocalTrackRepository(@ApplicationContext context: Context): LocalTrackRepository =
        LocalTrackRepository(context)

    @Singleton
    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Singleton
    @Provides
    fun provideNotificationHelper(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): NotificationHelper {
        return NotificationHelper(context, exoPlayer)
    }

    @Singleton
    @Provides
    fun provideMediaSession(@ApplicationContext context: Context): MediaSessionCompat{
        return MediaSessionCompat(context, "MusicService")
    }
}
