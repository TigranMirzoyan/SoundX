package com.soundx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soundx.databinding.YoutubeVideoRecyclerviewItemBinding
import com.soundx.model.youtube.YouTubeVideo
import com.soundx.view.adapter.SearchAdapter.YouTubeVideoViewHolder

class SearchAdapter : ListAdapter<YouTubeVideo, YouTubeVideoViewHolder>(YouTubeVideoDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YouTubeVideoViewHolder {
        val binding = YoutubeVideoRecyclerviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return YouTubeVideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YouTubeVideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
    }

    inner class YouTubeVideoViewHolder(private val binding: YoutubeVideoRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: YouTubeVideo) {
            binding.title.text = video.title
            binding.creator.text = video.creator
            Glide.with(binding.image.context)
                .load(video.thumbnailUrl)
                .centerCrop()
                .into(binding.image)
        }
    }


    class YouTubeVideoDiffCallBack : DiffUtil.ItemCallback<YouTubeVideo>() {
        override fun areItemsTheSame(oldItem: YouTubeVideo, newItem: YouTubeVideo): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: YouTubeVideo, newItem: YouTubeVideo): Boolean {
            return oldItem == newItem
        }
    }
}