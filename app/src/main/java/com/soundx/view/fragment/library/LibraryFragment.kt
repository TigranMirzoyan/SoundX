package com.soundx.view.fragment.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.soundx.model.database.playlist.Playlist
import com.soundx.databinding.FragmentLibraryBinding
import com.soundx.util.DialogFragments
import com.soundx.util.Fragments
import com.soundx.util.NavigationManager
import com.soundx.view.adapter.LibraryAdapter
import com.soundx.viewmodel.PlaylistViewModel


class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var viewModel: PlaylistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureButtons()
        configureRecyclerView()
    }

    private fun configureButtons() {
        binding.addPlaylist.setOnClickListener { NavigationManager.navigateToFragment(Fragments.CREATE_PLAYLIST_FRAGMENT) }
    }

    private fun configureRecyclerView() {
        val adapter = LibraryAdapter(onPlaylistClick(), onOptionsClick())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.allPlaylists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
        }
    }

    private fun onPlaylistClick(): (Playlist) -> Unit = {
        // will do later
    }

    private fun onOptionsClick(): (Playlist) -> Unit = { playlist ->
        val bundle = Bundle().apply {
            putInt("id", playlist.id)
            putString("name", playlist.name)
            putString("bio", playlist.bio)
            putString("imagePath", playlist.imagePath)
        }
        NavigationManager.navigateToFragment(DialogFragments.PLAYLIST_DIALOG_FRAGMENT, bundle)
    }
}