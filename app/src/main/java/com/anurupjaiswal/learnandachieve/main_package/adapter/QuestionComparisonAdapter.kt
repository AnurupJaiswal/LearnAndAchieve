package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemQuestionBinding
import com.anurupjaiswal.learnandachieve.model.QuestionItem
/*

class QuestionComparisonAdapter(
    private var questions: List<QuestionItem>,
    private var startIndex: Int = 1
) : RecyclerView.Adapter<QuestionComparisonAdapter.QuestionViewHolder>() {

    private val selectedAnswers = mutableMapOf<String, String>() // Store selected answer IDs

    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

*/
/*
        fun bind(question: QuestionItem, position: Int) {
            val questionNumber = startIndex + position // Maintain numbering across subjects

            // Convert HTML question text into formatted text
            binding.tvQuestion.text = "Question $questionNumber"
            binding.tvQuestionText.text = parseHtml(question.question)

            binding.llOptions.removeAllViews() // Clear previous options

            // Display main question options
            question.options.forEachIndexed { index, option ->
                addCheckBox(binding.llOptions, question.questionId, index, option)
            }

            // Handle sub-questions if present
            question.subQuestions?.forEach { subQuestion ->
                val subTextView = TextView(binding.root.context)
                subTextView.text = parseHtml(subQuestion.question)
                subTextView.textSize = 16f
            //    subTextView.setPadding(10, 10, 10, 0)
                binding.llOptions.addView(subTextView)

                subQuestion.options.forEachIndexed { index, option ->
                    addCheckBox(binding.llOptions, subQuestion.subQuestionId, index, option)
                }
            }
        }
*//*



        fun bind(question: QuestionItem, position: Int) {
            var questionNumber = startIndex // Start numbering from given index
            var totalSubQuestionsBefore = 0 // Track all sub-questions before this question

            // Count previous sub-questions correctly
            for (i in 0 until position) {
                totalSubQuestionsBefore += questions[i].subQuestions?.size ?: 0
            }

            // Ensure correct numbering for the main question
            questionNumber = startIndex + position + totalSubQuestionsBefore

            binding.tvQuestion.text = "Question $questionNumber"
            binding.tvQuestionText.text = parseHtml(question.question)
            binding.llOptions.removeAllViews()

            // Add main question options
            question.options.forEachIndexed { index, option ->
                addCheckBox(binding.llOptions, question.questionId, index, option)
            }

            // Handle sub-questions with proper decimal numbering
            question.subQuestions?.forEachIndexed { subIndex, subQuestion ->
                val subTextView = TextView(binding.root.context)
                subTextView.text = parseHtml(subQuestion.question)
                subTextView.textSize = 16f

                // Number sub-questions as: 2.1, 2.2, etc.
                val subQuestionNumber = "$questionNumber.${subIndex + 1}"
                subTextView.text = "$subQuestionNumber: ${subTextView.text}"
                binding.llOptions.addView(subTextView)

                subQuestion.options.forEachIndexed { index, option ->
                    addCheckBox(binding.llOptions, subQuestion.subQuestionId, index, option)
                }
            }
        }

        private fun addCheckBox(parent: ViewGroup, questionId: String, index: Int, option: String) {
            val checkBox = LayoutInflater.from(parent.context)
                .inflate(com.anurupjaiswal.learnandachieve.R.layout.item_checkbox_option, parent, false) as CheckBox

            val formattedOption = "${('A' + index)}. ${parseHtml(option)}"

            checkBox.text = formattedOption
            checkBox.isChecked = selectedAnswers[questionId] == option

            checkBox.setOnClickListener {
                selectedAnswers[questionId] = option
                notifyDataSetChanged() // Refresh to update the selection
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
        startIndex = newStartIndex // Ensure numbering continues
        notifyDataSetChanged()
    }

    fun getSelectedAnswers(): Map<String, String> {
        return selectedAnswers
    }

    // Helper function to parse HTML content
    private fun parseHtml(htmlText: String?): String {
        if (htmlText.isNullOrEmpty()) return ""

        // Remove HTML tags like <p>, <span>, etc., and trim spaces
        val plainText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString().trim()

        // Replace multiple spaces/newlines with a single space
        return plainText.replace(Regex("\\s+"), " ")
    }



}
*/


class QuestionComparisonAdapter(
    private var questions: List<QuestionItem>,
    private var startIndex: Int = 1
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

        private fun addCheckBox(parent: ViewGroup, questionId: String, index: Int, option: String) {
            val checkBox = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_checkbox_option, parent, false) as CheckBox

            val formattedOption = "${('A' + index)}. ${parseHtml(option)}"
            checkBox.text = formattedOption
            checkBox.isChecked = selectedAnswers[questionId] == option

            checkBox.setOnClickListener {
                selectedAnswers[questionId] = option
                notifyDataSetChanged() // Refresh to update the selection
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

    // ✅ Correct numbering logic with (sub-question count - 1)
    private fun generateCorrectNumbers() {
        var currentNumber = startIndex

        questions.forEachIndexed { index, question ->
            questionNumbers[index] = currentNumber
            val subQuestionCount = question.subQuestions?.size ?: 0
            currentNumber += 1 + maxOf(0, subQuestionCount - 1) // ✅ Adjust count
        }
    }

    // ✅ Parse HTML text safely
    private fun parseHtml(htmlText: String?): String {
        return if (htmlText.isNullOrEmpty()) ""
        else Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString().trim()
            .replace(Regex("\\s+"), " ") // Remove unnecessary spaces
    }
}
