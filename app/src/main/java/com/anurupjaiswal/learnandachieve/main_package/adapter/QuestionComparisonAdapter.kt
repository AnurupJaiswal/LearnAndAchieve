package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemQuestionBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.QuestionComparisonFragment
import com.anurupjaiswal.learnandachieve.model.QuestionItem

class QuestionComparisonAdapter(
    private var questions: List<QuestionItem>,
    private var startIndex: Int = 1,
    private val onAnswerSelected: (questionId: String, answer: String) -> Unit
) : RecyclerView.Adapter<QuestionComparisonAdapter.QuestionViewHolder>() {

    private val selectedAnswers = mutableMapOf<String, String>() // Store selected answers
    private val questionNumbers = mutableMapOf<Int, Int>() // Stores correct numbering for each question

    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionItem, position: Int) {
            val questionNumber = questionNumbers[position] ?: 1

            binding.tvQuestion.text = "Question $questionNumber"
            binding.tvQuestionText.text = parseHtml(question.question)
            binding.llOptions.removeAllViews()

            // Add main question options
            question.options.forEachIndexed { index, option ->
                addCheckBox(binding.llOptions, question.questionId, index, option)
            }

            // Handle sub-questions with proper numbering
            question.subQuestions?.forEachIndexed { subIndex, subQuestion ->
                val subTextView = TextView(binding.root.context)
                subTextView.text = parseHtml(subQuestion.question)
                subTextView.textSize = 16f

                val subQuestionNumber = "${subIndex + 1}"
                subTextView.text = "$subQuestionNumber: ${subTextView.text}"
                binding.llOptions.addView(subTextView)
                subTextView.typeface = ResourcesCompat.getFont(binding.root.context, R.font.gilroy_semibold)
subTextView.textSize = 14f
                subQuestion.options.forEachIndexed { index, option ->
                    addCheckBox(binding.llOptions, subQuestion.subQuestionId, index, option)
                }
            }
        }


/*
        private fun addCheckBox(parent: ViewGroup, questionId: String, index: Int, option: String) {
            val checkBox = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_checkbox_option, parent, false) as CheckBox

            // Create the formatted text for display.
            val formattedOption = "${('A' + index)}. ${parseHtml(option)}"
            checkBox.text = formattedOption
            // Compare using the stored numeric index (as String)
            checkBox.isChecked = selectedAnswers[questionId] == "$index"

            checkBox.setOnClickListener {
                // If this option is already selected, unselect it.
                if (selectedAnswers[questionId] == "$index") {
                    selectedAnswers.remove(questionId)
                    onAnswerSelected(questionId, "")
                } else {
                    // Otherwise, store the index (as String).
                    selectedAnswers[questionId] = "$index"
                    onAnswerSelected(questionId, "$index")
                }
                // Refresh so that only one checkbox appears selected.
                notifyDataSetChanged()
            }
            parent.addView(checkBox)
        }
*/


        private fun addCheckBox(parent: ViewGroup, questionId: String, index: Int, option: String) {
            val checkBox = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_checkbox_option, parent, false) as CheckBox

            val formattedOption = "${('A' + index)}. ${parseHtml(option)}"
            checkBox.text = formattedOption

            // Check if the stored answer equals the option index (as string)
            checkBox.isChecked = selectedAnswers[questionId] == "$index"

            checkBox.setOnClickListener {
                if (selectedAnswers[questionId] == "$index") {
                    // Unselect the option if already selected
                    selectedAnswers.remove(questionId)
                    onAnswerSelected(questionId, "")
                } else {
                    // Store the option index as a string (e.g. "0", "1", etc.)
                    selectedAnswers[questionId] = "$index"
                    onAnswerSelected(questionId, "$index")
                }
                // Refresh the adapter so only one checkbox is selected.
                notifyDataSetChanged()
            }
            parent.addView(checkBox)
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size

    fun updateData(newQuestions: List<QuestionItem>, newStartIndex: Int) {
        questions = newQuestions
        startIndex = newStartIndex
        generateCorrectNumbers()
        notifyDataSetChanged()
    }

    fun getSelectedAnswers(): Map<String, String> = selectedAnswers

    private fun generateCorrectNumbers() {
        var currentNumber = startIndex

        questions.forEachIndexed { index, question ->
            questionNumbers[index] = currentNumber

            val subQuestionCount = question.subQuestions?.size ?: 0
            // If sub‑questions exist, the effective increment is the number of sub‑questions.
            // Otherwise, it's 1.
            val increment = if (subQuestionCount > 0) subQuestionCount else 1
            currentNumber += increment
        }
    }


    // ✅ Parse HTML text safely
    private fun parseHtml(htmlText: String?): String {
        return if (htmlText.isNullOrEmpty()) ""
        else Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString().trim()
            .replace(Regex("\\s+"), " ") // Remove unnecessary spaces
    }
}
