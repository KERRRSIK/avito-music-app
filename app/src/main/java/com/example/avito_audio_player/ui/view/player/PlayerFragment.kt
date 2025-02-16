package com.example.avito_audio_player.ui.view.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.avito_audio_player.R
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.example.avito_audio_player.databinding.FragmentPlayerBinding
import com.example.avito_audio_player.service.MusicService
import com.example.avito_audio_player.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private var musicService: MusicService? = null
    private var serviceBound = false
    private val handler = Handler(Looper.getMainLooper())

    private var trackList: List<UnifiedTrack> = emptyList()
    private var currentTrackIndex: Int = 0

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            musicService = (binder as MusicService.MusicServiceBinder).getService()
            serviceBound = true
            if (trackList.isNotEmpty()) {
                musicService?.setTracks(trackList, currentTrackIndex)
                updateUIForTrack(trackList[currentTrackIndex])
                binding.play.setImageResource(
                    if (musicService?.isPlaying() == true) R.drawable.ic_play else R.drawable.ic_pause
                )
                updateProgress()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            musicService = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateProgress()
        binding.currentTime.text = formatTime(0)
        binding.endTime.text = formatTime(0)
        binding.seekBar.progress = 0
        binding.seekBar.max = 100

        trackList = arguments?.getParcelableArrayList("TRACK_LIST") ?: emptyList()
        currentTrackIndex = arguments?.getInt("CURRENT_TRACK_INDEX") ?: 0

        val serviceIntent = Intent(requireContext(), MusicService::class.java)
        requireContext().startService(serviceIntent)
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        binding.play.setOnClickListener {
            musicService?.let { service ->
                if (service.isPlaying()) {
                    service.pause()
                    binding.play.setImageResource(R.drawable.ic_play)
                } else {
                    service.play()
                    binding.play.setImageResource(R.drawable.ic_pause)
                }
            }
        }

        binding.next.setOnClickListener {
            musicService?.playNextTrack()
            if (trackList.isNotEmpty()) {
                currentTrackIndex = (currentTrackIndex + 1) % trackList.size
                updateUIForTrack(trackList[currentTrackIndex])
            }
        }

        binding.previous.setOnClickListener {
            musicService?.playPreviousTrack()
            if (trackList.isNotEmpty()) {
                currentTrackIndex = if (currentTrackIndex - 1 < 0) trackList.size - 1 else currentTrackIndex - 1
                updateUIForTrack(trackList[currentTrackIndex])
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress.toLong())
                    binding.currentTime.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacksAndMessages(null)
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateProgress()
            }
        })
    }

    private fun updateProgress() {
        musicService?.let { service ->
            val currentPos = service.getCurrentPosition().toInt()
            val duration = service.getDuration().toInt()
            binding.seekBar.max = duration
            binding.seekBar.progress = currentPos
            binding.currentTime.text = formatTime(currentPos)
            binding.endTime.text = formatTime(duration)

            handler.postDelayed({ updateProgress() }, 500)
        }
    }


    private fun formatTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateUIForTrack(track: UnifiedTrack) {
        binding.songName.text = if (!track.title.isNullOrBlank()) track.title else "Неизвестный трек"
        binding.artistName.text = if (!track.artist.isNullOrBlank()) track.artist else "Неизвестный артист"
        binding.songImage.loadImage(track.coverUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (serviceBound) {
            requireContext().unbindService(serviceConnection)
            serviceBound = false
        }
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}