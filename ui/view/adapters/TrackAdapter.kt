package com.example.avito_audio_player.ui.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.example.avito_audio_player.databinding.ItemTrackBinding
import com.example.avito_audio_player.utils.loadImage

class TrackAdapter(
    private val onTrackClicked: (UnifiedTrack) -> Unit
) : ListAdapter<UnifiedTrack, TrackAdapter.TrackViewHolder>(DiffCallback) {

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: UnifiedTrack) {
            binding.trackTitle.text = track.title
            binding.trackArtist.text = track.artist
            binding.trackCover.loadImage(track.coverUri)

            binding.root.setOnClickListener { onTrackClicked(track) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UnifiedTrack>() {
        override fun areItemsTheSame(oldItem: UnifiedTrack, newItem: UnifiedTrack): Boolean =
            oldItem.preview == newItem.preview

        override fun areContentsTheSame(oldItem: UnifiedTrack, newItem: UnifiedTrack): Boolean =
            oldItem == newItem
    }
}
