package com.soundx.view.fragment.library

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.soundx.R
import com.soundx.database.Playlist
import com.soundx.databinding.FragmentCreateBinding
import com.soundx.util.Fragments
import com.soundx.util.ImageStorageManager
import com.soundx.view.navigation.NavigationManager
import com.soundx.viewmodel.PlaylistViewModel
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreateBinding
    private lateinit var viewModel: PlaylistViewModel
    private lateinit var pickImageActivityResult: ActivityResultLauncher<String>
    private lateinit var cropActivityResult: ActivityResultLauncher<Intent>
    private var filePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]
        pickImage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureButtons()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) removeImage()
    }

    private fun configureButtons() {
        binding.create.setOnClickListener { create() }
        binding.cancel.setOnClickListener { cancel() }
        binding.remove.setOnClickListener { removeImage() }
        binding.pickImage.setOnClickListener { pickImageActivityResult.launch("image/*") }
    }

    private fun create() {
        val playlistName = binding.name.text.toString().trim()

        if (playlistName.isEmpty() || playlistName.length < 4) {
            Toast.makeText(requireContext(), "Name must be at least 4 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        if (filePath.isEmpty()) {
            filePath = "android.resource://com.soundx/${R.drawable.default_image}"
        }

        val playlist = Playlist(
            name = playlistName,
            bio = binding.bio.text.toString(),
            imagePath = filePath,
            songsNum = 0
        )

        viewModel.add(playlist)
        binding.name.text.clear()
        binding.bio.text.clear()
        putDefaultImage()
        NavigationManager.navigateToFragment(Fragments.LIBRARY_FRAGMENT)
    }

    private fun cancel() {
        deleteImage()
        binding.name.text.clear()
        binding.bio.text.clear()
        NavigationManager.navigateToFragment(Fragments.LIBRARY_FRAGMENT)
    }

    private fun removeImage() {
        deleteImage()
        putDefaultImage()
    }

    private fun pickImage() {
        pickImageActivityResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { crop(it) }
        }

        cropActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val croppedUri = UCrop.getOutput(result.data!!)
                croppedUri?.let { saveAndDisplayImage(it) }
            }
        }
    }

    private fun crop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(256, 256)

        cropActivityResult.launch(uCrop.getIntent(requireContext()))
    }

    private fun saveAndDisplayImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (filePath.isNotEmpty()) ImageStorageManager.deleteImageFromInternalStorage(filePath)
            filePath = ImageStorageManager.saveImageToInternalStorage(uri, requireContext()) ?: ""
            withContext(Dispatchers.Main) {
                binding.playlistImage.setImageURI(null)
                binding.playlistImage.setImageURI(uri)
                binding.playlistImage.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tint)
                )
                binding.remove.visibility = View.VISIBLE
            }
        }
    }

    private fun deleteImage() {
        if (filePath.isEmpty()) return
        lifecycleScope.launch(Dispatchers.IO) {
            ImageStorageManager.deleteImageFromInternalStorage(filePath)
            filePath = ""
        }
    }

    private fun putDefaultImage() {
        filePath = ""
        binding.playlistImage.setImageDrawable(null)
        binding.playlistImage.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.background_change_picture)
        )
        binding.playlistImage.setColorFilter("#00000000".toColorInt())
        binding.remove.visibility = View.GONE
    }
}