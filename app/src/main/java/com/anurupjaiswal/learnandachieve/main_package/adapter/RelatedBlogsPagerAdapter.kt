package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemRelatedBlogBinding
import com.anurupjaiswal.learnandachieve.model.RelatedBlog

class RelatedBlogPagerAdapter(
    private val blogs: List<RelatedBlog>,
    private val onItemClick: (RelatedBlog) -> Unit
) : RecyclerView.Adapter<RelatedBlogPagerAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(private val binding: ItemRelatedBlogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blog: RelatedBlog) {
            binding.titleTextView.text = blog.title
            binding.tvPostBy.text = blog.name
            binding.dateTextView.text = Utils.formatDate(blog.date)




            binding.details.text = Html.fromHtml(blog.details, Html.FROM_HTML_MODE_LEGACY)
            Utils.Picasso(blog.mainImage, binding.imageView, R.drawable.image_blog_test)

            binding.tvReadMore.setOnClickListener { onItemClick(blog) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding =
            ItemRelatedBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount(): Int = blogs.size
}