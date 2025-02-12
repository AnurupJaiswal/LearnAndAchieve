package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemQuestionStatusBinding
import com.anurupjaiswal.learnandachieve.model.QuestionItem
import com.google.android.material.card.MaterialCardView

enum class QuestionStatus {
    ANSWERED, NOT_ANSWERED, NOT_VIEWED
}

/*

class QuestionStatusAdapter(
    private var questionStatuses: List<QuestionStatus>,
    private val onQuestionClicked: (Int) -> Unit
) : RecyclerView.Adapter<QuestionStatusAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(private val binding: ItemQuestionStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(status: QuestionStatus, position: Int) {
            binding.tvQuestionNumber.text = (position + 1).toString()

            // Reset background first to avoid RecyclerView reusing old backgrounds
            binding.tvQuestionNumber.setBackgroundResource(0)

            // Change background based on status
            when (status) {
                QuestionStatus.ANSWERED -> binding.tvQuestionNumber.setBackgroundResource(R.drawable.bg_answered)
                QuestionStatus.NOT_ANSWERED -> binding.tvQuestionNumber.setBackgroundResource(R.drawable.bg_not_answered)
                QuestionStatus.NOT_VIEWED -> binding.tvQuestionNumber.setBackgroundResource(R.drawable.bg_not_viewed)
            }

            // Click listener for navigation
            binding.root.setOnClickListener {
                onQuestionClicked(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemQuestionStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(questionStatuses[position], position)
    }

    override fun getItemCount(): Int = questionStatuses.size

    fun updateData(newStatuses: List<QuestionStatus>) {
        questionStatuses = newStatuses
        notifyDataSetChanged()
    }

}
*/


class QuestionStatusAdapter(
    private var questionStatuses: List<QuestionStatus>,
    private val onQuestionClicked: (Int) -> Unit
) : RecyclerView.Adapter<QuestionStatusAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(private val binding: ItemQuestionStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(status: QuestionStatus, position: Int) {
            binding.tvQuestionNumber.text = (position + 1).toString()
            // Reset background first
            binding.tvQuestionNumber.setBackgroundResource(0)
            // Set background based on status
            when (status) {
                QuestionStatus.ANSWERED ->
                    binding.tvQuestionNumber.setBackgroundResource(R.drawable.bg_answered)
                QuestionStatus.NOT_ANSWERED ->
                    binding.tvQuestionNumber.setBackgroundResource(R.drawable.bg_not_answered)
                QuestionStatus.NOT_VIEWED ->
                    binding.tvQuestionNumber.setBackgroundResource(R.drawable.bg_not_viewed)
            }
            binding.root.setOnClickListener {
                onQuestionClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemQuestionStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(questionStatuses[position], position)
    }

    override fun getItemCount(): Int = questionStatuses.size

    fun updateData(newStatuses: List<QuestionStatus>) {
        questionStatuses = newStatuses
        notifyDataSetChanged()
    }
}