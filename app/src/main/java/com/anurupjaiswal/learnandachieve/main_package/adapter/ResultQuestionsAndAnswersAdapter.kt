package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.graphics.Typeface
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemOptionCheckboxResultBinding
import com.anurupjaiswal.learnandachieve.databinding.ItemResultQuestionsAndAnswersBinding
import com.anurupjaiswal.learnandachieve.model.Question
import com.anurupjaiswal.learnandachieve.model.ShowResultSubQuestion

class ResultQuestionsAndAnswersAdapter(
    private val questions: List<Question>,
    // startIndex is the continuous numbering start for the current subject.
    private val startIndex: Int = 1
) : RecyclerView.Adapter<ResultQuestionsAndAnswersAdapter.QuestionViewHolder>() {

    // Initialize as false so that solution is hidden initially.
    private val expandedSolutionStates = BooleanArray(questions.size) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemResultQuestionsAndAnswersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size

    inner class QuestionViewHolder(private val binding: ItemResultQuestionsAndAnswersBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question, position: Int) {
            // Main question header and text.
            binding.tvQuestionHeader.text = "Question ${startIndex + position} (2 Marks)"
            binding.tvQuestion.text = parseHtml(question.question)
            binding.llOptionsContainer.removeAllViews()

            // Display main question options using view binding.
            displayOptions(
                container = binding.llOptionsContainer,
                options = question.options,
                selectedOption = question.selectedOption,
                correctIndex = question.correctOption,
                letters = listOf("A", "B", "C", "D")
            )

            // If sub-questions exist, update main question status and hide its solution.
            if (!question.subQuestions.isNullOrEmpty()) {
                // Hide the main question solution and its toggle.
                binding.tvSolution.visibility = View.GONE
                binding.tvSolutionToggle.visibility = View.GONE

                val subQuestions = question.subQuestions!!
                if (subQuestions.all { it.selectedOption == null }) {
                    binding.tvAnswerStatus.text = "Not Attempted"
                    binding.tvAnswerStatus.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.grey)
                    )
                    binding.tvAnswerStatus.visibility = View.VISIBLE
                } else {
                    binding.tvAnswerStatus.visibility = View.GONE
                }

                // (Optional) Add a header for sub-questions if desired.
                val subHeader = TextView(binding.root.context).apply {
                    text = "" // Customize header text if needed.
                    textSize = 15f
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 0, 0, 8)
                }
                binding.llOptionsContainer.addView(subHeader)

                // Display each sub-question.
                question.subQuestions.forEachIndexed { subIndex, subQuestion ->
                    displaySubQuestion(subQuestion, subIndex)
                }
            } else {
                // For questions without sub-questions, show overall status only if not attempted.
                if (question.selectedOption == null) {
                    binding.tvAnswerStatus.text = "Not Attempted"
                    binding.tvAnswerStatus.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.grey)
                    )
                    binding.tvAnswerStatus.visibility = View.VISIBLE
                } else {
                    binding.tvAnswerStatus.visibility = View.GONE
                }

                // Set main question solution.
                binding.tvSolution.text = if (question.solution.isNotEmpty()) {
                    parseHtml(question.solution)
                } else {
                    "No solution provided."
                }
                val isExpanded = expandedSolutionStates[position]
                binding.tvSolution.visibility = if (isExpanded) View.VISIBLE else View.GONE
                setSolutionToggleText(binding.tvSolutionToggle, isExpanded)
                binding.tvSolutionToggle.setOnClickListener {
                    expandedSolutionStates[position] = !expandedSolutionStates[position]
                    notifyItemChanged(position)
                }
            }
        }

        private fun displayOptions(
            container: LinearLayout,
            options: List<String>,
            selectedOption: Int?,  // 0-based user-selected option index (null if not attempted)
            correctIndex: Int,     // 0-based correct answer index
            letters: List<String>
        ) {
            container.removeAllViews()
            for (index in options.indices) {
                // Inflate option layout using view binding.
                val optionBinding = ItemOptionCheckboxResultBinding.inflate(
                    LayoutInflater.from(container.context),
                    container,
                    false
                )

                optionBinding.tvAnswerStatus.visibility = View.VISIBLE
                optionBinding.tvAnswerStatus.setTextColor(
                    ContextCompat.getColor(optionBinding.root.context, R.color.black)
                )
                optionBinding.cbOption.buttonDrawable = null
                optionBinding.cbOption.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.checkbox_default, 0, 0, 0
                )
                optionBinding.cbOption.setTextColor(
                    ContextCompat.getColor(optionBinding.root.context, R.color.black)
                )


                val prefix = if (index < letters.size) "${letters[index]}. " else ""
                optionBinding.tvOptionText.text = parseHtml(prefix + options[index])

                // Set icons and text colors based on selection.
                if (selectedOption == null) {
                    if (index == correctIndex) {
                        optionBinding.cbOption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_tick, 0, 0, 0
                        )
                        optionBinding.tvOptionText.setTextColor(
                            ContextCompat.getColor(optionBinding.root.context, R.color.green)
                        )
                        optionBinding.tvAnswerStatus.text = "Correct Answer "
                        optionBinding.tvAnswerStatus.setTextColor(
                            ContextCompat.getColor(optionBinding.root.context, R.color.green)
                        )
                    }
                } else {
                    if (index == selectedOption) {
                        // User selected this option.
                        if (selectedOption == correctIndex) {
                            optionBinding.cbOption.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_tick, 0, 0, 0
                            )
                            optionBinding.tvOptionText.setTextColor(
                                ContextCompat.getColor(optionBinding.root.context, R.color.green)
                            )
                            optionBinding.tvAnswerStatus.text = "Correct Answer "
                            optionBinding.tvAnswerStatus.setTextColor(
                                ContextCompat.getColor(optionBinding.root.context, R.color.green)
                            )
                        } else {
                            optionBinding.cbOption.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_cross, 0, 0, 0
                            )
                            optionBinding.tvOptionText.setTextColor(
                                ContextCompat.getColor(optionBinding.root.context, R.color.red)
                            )
                            optionBinding.tvAnswerStatus.text = "Incorrect Answer "
                            optionBinding.tvAnswerStatus.setTextColor(
                                ContextCompat.getColor(optionBinding.root.context, R.color.red)
                            )
                        }
                    } else if (index == correctIndex) {
                        optionBinding.cbOption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_tick, 0, 0, 0
                        )
                        optionBinding.tvOptionText.setTextColor(
                            ContextCompat.getColor(optionBinding.root.context, R.color.green)
                        )
                        optionBinding.tvAnswerStatus.text = "Correct Answer "
                        optionBinding.tvAnswerStatus.setTextColor(
                            ContextCompat.getColor(optionBinding.root.context, R.color.green)
                        )
                    }
                }

                optionBinding.cbOption.isEnabled = false
                container.addView(optionBinding.root)
            }
        }
        private fun displaySubQuestion(subQuestion: ShowResultSubQuestion, subIndex: Int) {
            val inflater = LayoutInflater.from(binding.root.context)
            val subBinding = ItemResultQuestionsAndAnswersBinding.inflate(
                inflater,
                binding.llOptionsContainer,
                false
            )

            subBinding.llHeader.visibility = View.GONE
            subBinding.tvQuestion.text = "${subIndex + 1}. ${parseHtml(subQuestion.question)}"
            // Clear any pre-existing options.
            subBinding.llOptionsContainer.removeAllViews()

            // Display sub-question options.
            displayOptions(
                container = subBinding.llOptionsContainer,
                options = subQuestion.options,
                selectedOption = subQuestion.selectedOption,
                correctIndex = subQuestion.correctOption,
                letters = listOf("A", "B", "C", "D")
            )

            // Show sub-question status only if not attempted.
            if (subQuestion.selectedOption == null) {
                subBinding.tvAnswerStatus.text = "Not Attempted"
                subBinding.tvAnswerStatus.setTextColor(
                    ContextCompat.getColor(subBinding.root.context, R.color.grey)
                )
                subBinding.tvAnswerStatus.visibility = View.VISIBLE
            } else {
                subBinding.tvAnswerStatus.visibility = View.GONE
            }

            // Set and hide the sub-question solution by default.
            subBinding.tvSolution.text = if (subQuestion.solution.isNotEmpty()) {
                parseHtml(subQuestion.solution)
            } else {
                "No solution provided."
            }
            subBinding.tvSolution.visibility = View.GONE
            setSolutionToggleText(subBinding.tvSolutionToggle, false)
            subBinding.tvSolutionToggle.setOnClickListener {
                val currentlyVisible = subBinding.tvSolution.visibility == View.VISIBLE
                subBinding.tvSolution.visibility = if (currentlyVisible) View.GONE else View.VISIBLE
                setSolutionToggleText(subBinding.tvSolutionToggle, !currentlyVisible)
            }

            subBinding.root.setPadding(0, 0, 0, 0)
            binding.llOptionsContainer.addView(subBinding.root)
        }
    }

    private fun parseHtml(htmlText: String?): String {
        return if (htmlText.isNullOrEmpty()) ""
        else Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString().trim()
            .replace(Regex("\\s+"), " ") // Remove unnecessary spaces.
    }

    private fun setSolutionToggleText(tv: TextView, isExpanded: Boolean) {
        val context = tv.context
        val text = if (isExpanded) "Solution: Hide" else "Solution: Show"
        val spannable = SpannableString(text)
        val prefix = "Solution:"
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            prefix.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.black)),
            0,
            prefix.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val remainderStart = prefix.length + 1
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            remainderStart,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.primaryColor)),
            remainderStart,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.text = spannable
    }
}
