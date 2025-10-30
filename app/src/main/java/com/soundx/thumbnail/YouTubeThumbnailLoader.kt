package com.soundx.thumbnail

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.soundx.R

object YouTubeThumbnailLoader {

    fun loadThumbnail(
        videoId: String, quality: List<ThumbnailQuality>, target: ImageView, fragment: Fragment
    ) {
        loadThumbnail(videoId, quality, target, Glide.with(fragment))
    }

    fun loadThumbnail(
        videoId: String, quality: List<ThumbnailQuality>, target: ImageView, context: Context
    ) {
        loadThumbnail(videoId, quality, target, Glide.with(context))
    }

    private fun loadThumbnail(
        videoId: String,
        quality: List<ThumbnailQuality>,
        target: ImageView,
        requestManager: RequestManager
    ) {
        val res = "https://img.youtube.com/vi/$videoId/${quality.firstOrNull()?.id}.jpg"

        requestManager.load(
            if (quality.isEmpty()) {
                R.drawable.ic_song_default
            } else {
                res
            }
        ).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                targetDrawable: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                if (!quality.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        loadThumbnail(videoId, quality.drop(1), target, requestManager)
                    }
                    return true
                }

                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        }).centerCrop().into(target)
    }
}