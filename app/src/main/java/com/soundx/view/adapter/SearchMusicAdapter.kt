package com.soundx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soundx.R
import com.soundx.databinding.SearchMusicItemBinding
import com.soundx.util.YouTubeSong

class SearchMusicAdapter(private val onItemClicked: (Int) -> Unit) :
    ListAdapter<YouTubeSong, SearchMusicAdapter.YouTubeSongViewHolder>(YouTubeSongDiffCallBack) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YouTubeSongViewHolder {
        val binding = SearchMusicItemBinding.inflate(
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
            onItemClicked(position)
        }
    }

    inner class YouTubeSongViewHolder(private val binding: SearchMusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: YouTubeSong) {
            binding.title.text = song.title
            binding.creator.text = song.channelTitle

            val thumbnailUrl = "https://img.youtube.com/vi/${song.videoId}/mqdefault.jpg"
            Glide.with(binding.thumbnail.context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_music_default)
                .error(R.drawable.ic_music_default)
                .centerCrop()
                .into(binding.thumbnail)
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
