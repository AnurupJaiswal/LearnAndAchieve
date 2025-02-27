package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemFaqQuestionBinding
import com.anurupjaiswal.learnandachieve.model.FAQQuestion


class QuestionsAdapter(
    private var questions: List<FAQQuestion>
) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    private var expandedPosition = -1  // Track the currently expanded position

    // Function to update questions dynamically
    fun updateQuestions(newQuestions: List<FAQQuestion>) {
        questions = newQuestions
        notifyDataSetChanged()
    }

    inner class QuestionViewHolder(val binding: ItemFaqQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemFaqQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.binding.tvQuestion.text = question.question
        holder.binding.tvAnswer.text = question.answer

        // Set the answer visibility based on whether this question is expanded
        val isExpanded = position == expandedPosition
        holder.binding.tvAnswer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Rotate the arrow based on the expanded state
        val rotationAngle = if (isExpanded) 90f else 0f
        holder.binding.imgArrow.rotation = rotationAngle

        // Handle click on the question to toggle the answer visibility

        holder.binding.tvQuestion.setOnClickListener {

            // Collapse the previously expanded question if necessary
            val previousExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) -1 else position
            // Notify the adapter to update the visibility
            notifyItemChanged(previousExpandedPosition)

            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount() = questions.size
}
