package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemFaqQuestionBinding
import com.anurupjaiswal.learnandachieve.databinding.ItemQuestionBinding
import com.anurupjaiswal.learnandachieve.model.FAQQuestion

/*
class QuestionsAdapter(
    private val questions: List<FAQQuestion>
) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    private var expandedPosition = -1

    inner class QuestionViewHolder(val binding: ItemFaqQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemFaqQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]

        holder.binding.tvQuestion.text = question.question
        holder.binding.tvAnswer.text = question.answer

        val isExpanded = position == expandedPosition
        holder.binding.tvAnswer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Rotate arrow based on expanded state
      */
/*  val rotationAngle = if (isExpanded) 270f else 0f
        holder.binding.imgArrow.rotation = rotationAngle*//*



        val startAngle = if (isExpanded) 0f else 270f
        val endAngle = if (isExpanded) 270f else 0f
        ObjectAnimator.ofFloat(holder.binding.imgArrow, "rotation", startAngle, endAngle).apply {
            duration = 300 // Animation duration in milliseconds
            start()
        }

        // Set click listener for question
        holder.binding.tvQuestion.setOnClickListener {
            val previousExpandedPosition = expandedPosition

            // Update the expanded position to the current one, or collapse if it's already expanded
            expandedPosition = if (isExpanded) -1 else position

            // Notify changes to collapse the previous expanded item and expand the new one
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount() = questions.size
}
*/



class QuestionsAdapter(
    private val questions: List<FAQQuestion>
) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    private var expandedPosition = -1

    inner class QuestionViewHolder(val binding: ItemFaqQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemFaqQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]

        holder.binding.tvQuestion.text = question.question
        holder.binding.tvAnswer.text = question.answer

        val isExpanded = position == expandedPosition
        holder.binding.tvAnswer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Rotate arrow based on expanded state
        val startAngle = if (isExpanded) 0f else 270f
        val endAngle = if (isExpanded) 270f else 0f
        ObjectAnimator.ofFloat(holder.binding.imgArrow, "rotation", startAngle, endAngle).apply {
            duration = 300 // Animation duration in milliseconds
            start()
        }

        // Get colors from colors.xml and set text color
        val context = holder.binding.tvQuestion.context
        val expandedColor = ContextCompat.getColor(context, R.color.blue)
        val collapsedColor = ContextCompat.getColor(context, R.color.black)

        holder.binding.tvQuestion.setTextColor(if (isExpanded) expandedColor else collapsedColor)

        // Set click listener for question
        holder.binding.tvQuestion.setOnClickListener {
            val previousExpandedPosition = expandedPosition

            // Update the expanded position to the current one, or collapse if it's already expanded
            expandedPosition = if (isExpanded) -1 else position

            // Notify changes to collapse the previous expanded item and expand the new one
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount() = questions.size
}