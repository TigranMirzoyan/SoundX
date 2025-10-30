package com.soundx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soundx.databinding.SongSearchItemBinding
import com.soundx.thumbnail.ThumbnailQuality
import com.soundx.thumbnail.YouTubeThumbnailLoader
import com.soundx.util.YouTubeSong

class SongSearchAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<YouTubeSong, SongSearchAdapter.YouTubeSongViewHolder>(YouTubeSongDiffCallBack) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YouTubeSongViewHolder {
        val binding = SongSearchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return YouTubeSongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YouTubeSongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    inner class YouTubeSongViewHolder(private val binding: SongSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: YouTubeSong) {
            binding.title.text = song.title
            binding.creator.text = song.channelTitle

            YouTubeThumbnailLoader.loadThumbnail(
                song.videoId,
                listOf(ThumbnailQuality.MEDIUM),
                binding.thumbnail,
                binding.thumbnail.context
            )
        }
    }

    object YouTubeSongDiffCallBack : DiffUtil.ItemCallback<YouTubeSong>() {
        override fun areItemsTheSame(oldItem: YouTubeSong, newItem: YouTubeSong): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: YouTubeSong, newItem: YouTubeSong): Boolean {
            return oldItem == newItem
        }
    }
}
