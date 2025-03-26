package com.soundx.view.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.soundx.databinding.FragmentPlaylistDialogBinding
import com.soundx.util.Fragments
import com.soundx.util.ImageStorageManager
import com.soundx.util.NavigationManager
import com.soundx.viewmodel.PlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPlaylistDialogBinding
    private lateinit var viewModel: PlaylistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureButtons()
    }

    private fun configureButtons() {
        binding.delete.setOnClickListener { delete() }
        binding.edit.setOnClickListener { edit() }
    }

    private fun delete() {
        viewModel.delete(arguments?.getInt("id")!!)
        lifecycleScope.launch(Dispatchers.IO) {
            ImageStorageManager.deleteImageFromInternalStorage(arguments?.getString("imagePath")!!)
        }
        dismiss()
    }

    private fun edit() {
        val bundle = Bundle().apply {
            putInt("id", arguments?.getInt("id") ?: -1)
            putString("name", arguments?.getString("name"))
            putString("bio", arguments?.getString("bio"))
            putString("imagePath", arguments?.getString("imagePath"))
        }
        NavigationManager.navigateToFragment(Fragments.EDIT_PLAYLIST_FRAGMENT, bundle)
        dismiss()
    }
}