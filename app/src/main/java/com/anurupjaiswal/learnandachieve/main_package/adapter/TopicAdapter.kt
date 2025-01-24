package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemTopicBinding
import com.anurupjaiswal.learnandachieve.model.Topic
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout

class TopicAdapter(
    private val context: Context,
    private val topics: List<Topic>,
    private val onTopicClick: (Topic) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = topics[position]

        // Check if YouTube links are available
        if (topic.youtube_links.isNotEmpty()) {
            val youtubeLink = topic.youtube_links[0]
            val videoId = extractVideoId(youtubeLink)
            if (videoId.isNotEmpty()) {
                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

                // Show shimmer effect
                holder.binding.shimmerLayout.startShimmer()
                holder.binding.shimmerLayout.visibility = View.VISIBLE
                holder.binding.youtubeThumbnail.visibility = View.INVISIBLE

                // Load the thumbnail using Glide with a listener
                Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.youtubethumnail) // Fallback image
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Hide shimmer and show placeholder
                            holder.binding.shimmerLayout.stopShimmer()
                            holder.binding.shimmerLayout.visibility = View.GONE
                            holder.binding.youtubeThumbnail.visibility = View.VISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Hide shimmer and show thumbnail
                            holder.binding.shimmerLayout.stopShimmer()
                            holder.binding.shimmerLayout.visibility = View.GONE
                            holder.binding.youtubeThumbnail.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(holder.binding.youtubeThumbnail)

                // Set click listener to play the video
                holder.itemView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
                    context.startActivity(intent)
                }
            } else {
                // If the video ID couldn't be extracted, show a default image
                handleInvalidThumbnail(holder)
            }
        } else {
            // Handle case where no YouTube links are available
            handleInvalidThumbnail(holder)
        }
    }

    override fun getItemCount(): Int = topics.size

    class TopicViewHolder(val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Extracts the video ID from a YouTube link.
     * Supports standard YouTube URLs (e.g., https://www.youtube.com/watch?v=VIDEO_ID)
     */
    private fun extractVideoId(youtubeLink: String): String {
        val regex = "(?<=v=|/videos/|embed/|youtu.be/)[^#&?]*".toRegex()
        val matchResult = regex.find(youtubeLink)
        return matchResult?.value ?: "" // Return video ID or empty string if not found
    }

    /**
     * Handles cases where the YouTube thumbnail or link is invalid.
     */
    private fun handleInvalidThumbnail(holder: TopicViewHolder) {
        holder.binding.shimmerLayout.stopShimmer()
        holder.binding.shimmerLayout.visibility = View.GONE
        holder.binding.youtubeThumbnail.setImageResource(R.drawable.youtubethumnail)
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "No YouTube link available", Toast.LENGTH_SHORT).show()
        }
    }
}
