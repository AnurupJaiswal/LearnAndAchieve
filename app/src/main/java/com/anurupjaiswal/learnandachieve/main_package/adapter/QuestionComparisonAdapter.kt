package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemQuestionBinding
import com.anurupjaiswal.learnandachieve.model.QuestionItem

class QuestionComparisonAdapter(private val questionList: List<QuestionItem>) : RecyclerView.Adapter<QuestionComparisonAdapter.QuestionViewHolder>() {

    // ViewHolder for binding the data
    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, questionItem: QuestionItem) {
            binding.apply {
                // Bind question data
                questionNumber.text = questionItem.questionNumber
                questionTitle.text = questionItem.questionTitle
                paragraphText.text = questionItem.paragraphText

                // Bind options
                option1.text = questionItem.option1
                option2.text = questionItem.option2
                option3.text = questionItem.option3
                option4.text = questionItem.option4

                // Set the checked state of the checkboxes based on the selected option index
                option1.isChecked = questionItem.selectedOptionIndex == 0
                option2.isChecked = questionItem.selectedOptionIndex == 1
                option3.isChecked = questionItem.selectedOptionIndex == 2
                option4.isChecked = questionItem.selectedOptionIndex == 3

                // Handle checkbox clicks to ensure only one option is selected at a time
                option1.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        questionItem.selectedOptionIndex = 0
                        option2.isChecked = false
                        option3.isChecked = false
                        option4.isChecked = false
                    } else {
                        questionItem.selectedOptionIndex = null
                    }
                }

                option2.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        questionItem.selectedOptionIndex = 1
                        option1.isChecked = false
                        option3.isChecked = false
                        option4.isChecked = false
                    } else {
                        questionItem.selectedOptionIndex = null
                    }
                }

                option3.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        questionItem.selectedOptionIndex = 2
                        option1.isChecked = false
                        option2.isChecked = false
                        option4.isChecked = false
                    } else {
                        questionItem.selectedOptionIndex = null
                    }
                }

                option4.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        questionItem.selectedOptionIndex = 3
                        option1.isChecked = false
                        option2.isChecked = false
                        option3.isChecked = false
                    } else {
                        questionItem.selectedOptionIndex = null
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(position, questionList[position])
    }

    override fun getItemCount(): Int = questionList.size
}
