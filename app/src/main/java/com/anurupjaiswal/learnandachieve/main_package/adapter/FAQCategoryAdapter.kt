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
    private var categories: List<FAQCategory>,
    private val onCategoryClicked: (categoryId: String, position: Int) -> Unit
) : RecyclerView.Adapter<FAQCategoryAdapter.CategoryViewHolder>() {

    private var expandedPosition = -1

    fun updateCategories(newCategories: List<FAQCategory>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }



    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryName.text = category.faqCategoryName

        // Set the text color based on whether the category is expanded or not
        val color = if (position == expandedPosition) {
            ContextCompat.getColor(holder.itemView.context, R.color.primaryColor)  // Blue when expanded
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.black)  // Black when not expanded
        }
        holder.binding.tvCategoryName.setTextColor(color)

        // Change the color of the viewCategory dynamically based on the expanded state
        val viewCategoryColor = if (position == expandedPosition) {
            ContextCompat.getColor(holder.itemView.context, R.color.primaryColor)  // Blue when expanded
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.black)  // Black when not expanded
        }
        holder.binding.viewCategory.setBackgroundColor(viewCategoryColor)

        // Ensure category.questions is not null before passing to the adapter
        val questions = category.questions ?: emptyList()  // Default to empty list if null
        val questionAdapter = QuestionsAdapter(questions)
        holder.binding.rvQuestions.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.rvQuestions.adapter = questionAdapter

        // Handle expand/collapse state (without animation)
        val isExpanded = position == expandedPosition
        holder.binding.rvQuestions.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Rotate the arrow for expand/collapse (without animation)
        val rotationAngle = if (isExpanded) 90f else 0f
        holder.binding.tvArrowForward.rotation = rotationAngle

        // Handle category click to expand/collapse
        holder.binding.llContainer.setOnClickListener {
            val previousExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(expandedPosition)

            val categoryId = category.faq_Category_id
            onCategoryClicked(categoryId, position)  // Trigger API to load questions for the category
        }
    }



    override fun getItemCount() = categories.size
}
