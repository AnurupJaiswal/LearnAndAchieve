package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemBlogBinding
import com.anurupjaiswal.learnandachieve.model.Blog



class BlogAdapter(private var blogList: List<Blog>) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(private val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(blog: Blog) {
            binding.titleTextView.text = blog.title
            binding.dateTextView.text = blog.date
            binding.descriptionTextView.text = blog.description
            // Glide.with(binding.root.context).load(blog.imageUrl).into(binding.imageView)

            binding.tvReadMore.setOnClickListener {
                val navController = itemView.findNavController()

                // Create the Bundle and pass the necessary data
                val bundle = Bundle().apply {
                    putString("title", blog.title)
                    putString("date", blog.date)
                    putString("description", blog.description)
                    putString("imageUrl", blog.imageUrl)  // If needed
                }

                navController.navigate(R.id.BlogDetailsFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogList[position])
    }

    override fun getItemCount() = blogList.size

    fun updateBlogs(newBlogs: List<Blog>) {
        blogList = newBlogs
        notifyDataSetChanged()
    }
}
