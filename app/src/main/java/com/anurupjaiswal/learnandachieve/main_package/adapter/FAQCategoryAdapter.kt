package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemCategoryBinding
import com.anurupjaiswal.learnandachieve.model.FAQCategory

class FAQCategoryAdapter(
    private val categories: List<FAQCategory>
) : RecyclerView.Adapter<FAQCategoryAdapter.CategoryViewHolder>() {

    // Track the currently expanded position (-1 means no category is expanded)
    private var expandedPosition = -1

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryName.text = category.categoryName

        // Set up the child RecyclerView for questions
        val questionAdapter = QuestionsAdapter(category.questions)
        holder.binding.rvQuestions.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.rvQuestions.adapter = questionAdapter

        // Update views based on expand/collapse state
        val isExpanded = position == expandedPosition
        holder.binding.rvQuestions.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Rotate arrow based on expanded state
        val rotationAngle = if (isExpanded) 90f else 0f
        holder.binding.tvArrowForward.rotation = rotationAngle

        // Update category name color
        val color = if (isExpanded) {
            ContextCompat.getColor(holder.itemView.context, R.color.primaryColor)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.black)
        }
        holder.binding.tvCategoryName.setTextColor(color)

        // Change divider color based on expanded state
        val dividerColor = if (isExpanded) {
            ContextCompat.getColor(holder.itemView.context, R.color.primaryColor)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.black)
        }
        holder.binding.viewCategory.setBackgroundColor(dividerColor)

        // Set onClick listener for the category name
        holder.binding.llContainer.setOnClickListener {
            val previousExpandedPosition = expandedPosition

            // Update the expanded position
            expandedPosition = if (isExpanded) -1 else position

            // Notify item changes to update visibility and animations
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount() = categories.size
}
