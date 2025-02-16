package com.example.avito_audio_player.ui.view.local_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avito_audio_player.R
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.example.avito_audio_player.databinding.FragmentLocalTracksBinding
import com.example.avito_audio_player.ui.view.main.MainActivity
import com.example.avito_audio_player.ui.view.adapters.TrackAdapter
import com.example.avito_audio_player.ui.viewmodel.LocalTracksViewModel
import com.example.avito_audio_player.utils.toUnifiedTrack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocalTracksFragment : Fragment(R.layout.fragment_local_tracks) {

    private var _binding: FragmentLocalTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocalTracksViewModel by viewModels()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter { track ->
            openPlayer(track)
        }
        binding.tracksAPIList.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksAPIList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredTracks.collectLatest { tracks ->
                adapter.submitList(tracks.map { it.toUnifiedTrack() })
            }
        }

        (activity as? MainActivity)?.setSearchQueryListener { query ->
            viewModel.searchTracks(query)
        }

        viewModel.loadLocalTracks()
    }

    private fun openPlayer(track: UnifiedTrack) {
        val trackList = ArrayList(viewModel.filteredTracks.value.map { it.toUnifiedTrack() })
        val currentIndex = trackList.indexOfFirst { it.id == track.id }
        val bundle = Bundle().apply {
            putParcelableArrayList("TRACK_LIST", trackList)
            putInt("CURRENT_TRACK_INDEX", currentIndex)
        }
        findNavController().navigate(R.id.action_localTracksFragment_to_playerFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
