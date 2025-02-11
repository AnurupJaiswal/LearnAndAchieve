package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemBlogBinding
import com.anurupjaiswal.learnandachieve.model.BlogData

class BlogAdapter(
    private var blogList: List<BlogData>,
    private val onItemClick: (BlogData) -> Unit
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]
        holder.bind(blog)
    }

    override fun getItemCount(): Int = blogList.size

    fun updateList(newList: List<BlogData>) {
        blogList = newList
        notifyDataSetChanged()
    }

    class BlogViewHolder(
        private val binding: ItemBlogBinding,
        private val onItemClick: (BlogData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(blog: BlogData) {
            binding.titleTextView.text = blog.title
            binding.tvPostBy.text = blog.name
            binding.dateTextView.text = Utils.formatDate(blog.date)
            binding.descriptionTextView.text = blog.briefIntro
            Utils.Picasso(blog.mainImage, binding.imageView, R.drawable.image_blog_test)

            // Apply click listener to the whole item

            binding.tvReadMore.setOnClickListener { onItemClick(blog) }
        }
    }
}
