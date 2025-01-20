package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemTopicBinding
import com.bumptech.glide.Glide

class TopicAdapter(
    private val context: Context,
    private val topics: List<com.anurupjaiswal.learnandachieve.model.Topic>,
    private val onTopicClick: (com.anurupjaiswal.learnandachieve.model.Topic) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int = topics.size

    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: com.anurupjaiswal.learnandachieve.model.Topic) {


            Glide.with(context)
                .load(topic.youtubeThumbnailUrl) // Load the thumbnail URL
                .into(binding.youtubeThumbnail)

            // Handle click event on the image to open the YouTube video
            binding.root.setOnClickListener {
                onTopicClick(topic)
            }
        }
    }
}
