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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.soundx.R
import com.soundx.databinding.FragmentEditBinding
import com.soundx.util.Fragments
import com.soundx.util.ImageStorageManager
import com.soundx.util.NavigationManager
import com.soundx.viewmodel.PlaylistViewModel
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var viewModel: PlaylistViewModel
    private lateinit var pickImageActivityResult: ActivityResultLauncher<String>
    private lateinit var cropActivityResult: ActivityResultLauncher<Intent>
    private var filePath = "old"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
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
        if (!hidden) setItems()
    }

    private fun configureButtons() {
        binding.edit.setOnClickListener { edit() }
        binding.cancel.setOnClickListener { cancel() }
        binding.remove.setOnClickListener { removeImage() }
        binding.pickImage.setOnClickListener { pickImageActivityResult.launch("image/*") }
    }

    private fun setItems() {
        binding.name.setText(arguments?.getString("name"))
        binding.bio.setText(arguments?.getString("bio"))
        binding.playlistImage.setImageURI(null)
        binding.playlistImage.setImageURI(arguments?.getString("imagePath")?.toUri())
        binding.playlistImage.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.tint)
        )
    }

    private fun edit() {
        val playlistName = binding.name.text.toString().trim()

        if (playlistName.isEmpty() || playlistName.length < 4) {
            Toast.makeText(requireContext(), "Name must be at least 4 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        if (filePath.isEmpty()) {
            filePath = "android.resource://com.soundx/${R.drawable.default_image}"
        } else if (filePath == "old") {
            filePath = arguments?.getString("imagePath")!!
        }

        if (id == -1) {
            Toast.makeText(requireContext(), "Playlist no longer exists", Toast.LENGTH_SHORT).show()
            cancel()
            return
        }

        viewModel.editPlaylist(
            arguments?.getInt("id")!!,
            filePath,
            playlistName,
            binding.bio.text.toString()
        )

        binding.name.text.clear()
        binding.bio.text.clear()
        filePath = "old"
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
            if (filePath.isNotEmpty() && filePath != "old") ImageStorageManager.deleteImageFromInternalStorage(filePath)
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
        if (filePath.isEmpty() || filePath == "old") return
        lifecycleScope.launch(Dispatchers.IO) {
            ImageStorageManager.deleteImageFromInternalStorage(filePath)
            filePath = "old"
        }
    }

    private fun putDefaultImage() {
        binding.playlistImage.setImageDrawable(null)
        binding.playlistImage.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.background_change_picture)
        )
        binding.playlistImage.setColorFilter("#00000000".toColorInt())
        binding.remove.visibility = View.GONE
    }
}