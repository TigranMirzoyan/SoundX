package com.soundx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soundx.R
import com.soundx.databinding.SearchVideoMusicItemBinding
import com.soundx.util.YouTubeVideo

class SearchVideosAdapter(private val onItemClicked: (Int) -> Unit) :
    ListAdapter<YouTubeVideo, SearchVideosAdapter.YouTubeVideoViewHolder>(YouTubeVideoDiffCallBack) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YouTubeVideoViewHolder {
        val binding = SearchVideoMusicItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return YouTubeVideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YouTubeVideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)

        holder.itemView.setOnClickListener {
            onItemClicked(position)
        }
    }

    inner class YouTubeVideoViewHolder(private val binding: SearchVideoMusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: YouTubeVideo) {
            binding.title.text = video.title
            binding.creator.text = video.channelTitle

            val thumbnailUrl = "https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg"
            Glide.with(binding.thumbnail.context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_music_default)
                .error(R.drawable.ic_music_default)
                .into(binding.thumbnail)
        }
    }

    object YouTubeVideoDiffCallBack : DiffUtil.ItemCallback<YouTubeVideo>() {
        override fun areItemsTheSame(oldItem: YouTubeVideo, newItem: YouTubeVideo): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: YouTubeVideo, newItem: YouTubeVideo): Boolean {
            return oldItem == newItem
        }
    }
}
