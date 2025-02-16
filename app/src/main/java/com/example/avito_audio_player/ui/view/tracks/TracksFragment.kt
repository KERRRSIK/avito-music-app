package com.example.avito_audio_player.ui.view.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avito_audio_player.R
import com.example.avito_audio_player.data.model.UnifiedTrack
import com.example.avito_audio_player.databinding.FragmentTracksBinding
import com.example.avito_audio_player.ui.view.main.MainActivity
import com.example.avito_audio_player.ui.view.adapters.TrackAdapter
import com.example.avito_audio_player.ui.viewmodel.TracksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TracksFragment : Fragment(R.layout.fragment_tracks) {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TracksViewModel by viewModels()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
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
                adapter.submitList(tracks)
            }
        }

        (activity as? MainActivity)?.setSearchQueryListener { query ->
            viewModel.searchTracks(query)
        }

        viewModel.loadTracks()
    }

    private fun openPlayer(track: UnifiedTrack) {
        val trackList = ArrayList(viewModel.filteredTracks.value)
        val currentIndex = trackList.indexOfFirst { it.preview == track.preview && it.title == track.title }

        val bundle = Bundle().apply {
            // Передаем список треков как ArrayList Parcelable
            putParcelableArrayList("TRACK_LIST", trackList)
            // Передаем индекс выбранного трека
            putInt("CURRENT_TRACK_INDEX", currentIndex)
            putString("TRACK_PREVIEW_URL", track.preview)
            putString("TRACK_TITLE", track.title)
            putString("TRACK_ARTIST", track.artist)
            putString("TRACK_COVER", track.coverUri)
        }
        findNavController().navigate(R.id.action_tracksFragment_to_playerFragment, bundle)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchTracks(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchTracks(newText.orEmpty())
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
