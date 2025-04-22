package com.soundx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soundx.model.database.playlist.Playlist
import com.soundx.databinding.PlaylistRecyclerviewItemBinding

class LibraryAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
    private val onOptionsClick: (Playlist) -> Unit
) : ListAdapter<Playlist, LibraryAdapter.PlaylistViewHolder>(PlaylistDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val binding = PlaylistRecyclerviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
    }

    inner class PlaylistViewHolder(private val binding: PlaylistRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.image.setImageURI(playlist.imagePath.toUri())
            binding.name.text = playlist.name
            if (playlist.songsNum != 0) binding.songsAmount.text = "${playlist.songsNum}"

            itemView.setOnClickListener { onPlaylistClick(playlist) }
            binding.more.setOnClickListener { onOptionsClick(playlist) }
        }
    }

    class PlaylistDiffCallBack : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}