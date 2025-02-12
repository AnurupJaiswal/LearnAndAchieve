package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentQuestionComparisonBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionComparisonAdapter

import com.anurupjaiswal.learnandachieve.model.QuestionComparisonResponse
import com.anurupjaiswal.learnandachieve.model.QuestionComparisonSubject
import com.anurupjaiswal.learnandachieve.model.QuestionItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionComparisonFragment : Fragment() {

    private var _binding: FragmentQuestionComparisonBinding? = null
    private val binding get() = _binding!!
    private var mockTestId: String? = null
    private lateinit var adapter: QuestionComparisonAdapter
    private var selectedSubjectId: String? = null
    private var mockTestData: QuestionComparisonResponse? = null

    private var currentQuestionIndex = 0
    private var currentSubjectIndex = 0
    private var allSubjects: List<QuestionComparisonSubject> = emptyList()
    private var currentQuestions: List<QuestionItem> = emptyList()
    private var questionNumberOffset = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mockTestId = arguments?.getString("mockTest_id")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcvQuestionComparison.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionComparisonAdapter(emptyList())
        binding.rcvQuestionComparison.adapter = adapter

        mockTestId?.let {
            fetchMockTestQuestions(it)
        } ?: Log.e("QuestionComparison", "MockTest ID is null!")

        binding.mcvPrevious.setOnClickListener { showPreviousQuestion() }
        binding.mcvNext.setOnClickListener { showNextQuestion() }
    }

    private fun fetchMockTestQuestions(mockTestId: String) {
        val apiService = RetrofitClient.client
        val token = Utils.GetSession().token

        apiService.getMockTestQuestions("Bearer $token", mockTestId).enqueue(object : Callback<QuestionComparisonResponse> {
            override fun onResponse(call: Call<QuestionComparisonResponse>, response: Response<QuestionComparisonResponse>) {
                if (response.isSuccessful) {
                    mockTestData = response.body()
                    mockTestData?.let { data ->
                        allSubjects = data.data.subjects
                        setupSubjectTabs(allSubjects)
                        val durationInMinutes = data.data.durationInMinutes ?: 0
                        Utils.startCountdownTimer(durationInMinutes, binding.tvTimer)

                        if (allSubjects.isNotEmpty()) {
                            calculateQuestionOffsets()
                            onSubjectSelected(0) // Select first subject by default
                        }
                    }
                } else {
                    Log.e("QuestionComparison", "API Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<QuestionComparisonResponse>, t: Throwable) {
                Log.e("QuestionComparison", "API Call Failed: ${t.message}")
            }
        })
    }

    private fun setupSubjectTabs(subjects: List<QuestionComparisonSubject>) {
        val subjectContainer = binding.subjectContainer
        subjectContainer.removeAllViews()

        for ((index, subject) in subjects.withIndex()) {
            val button = LayoutInflater.from(requireContext()).inflate(R.layout.item_subject_button, subjectContainer, false) as TextView
            button.text = subject.subjectName
            button.setOnClickListener {
                onSubjectSelected(index)
            }
            subjectContainer.addView(button)

            if (index == 0) {
                button.isSelected = true
            }
        }
    }

    private fun calculateQuestionOffsets() {
        questionNumberOffset = 0

        for (i in allSubjects.indices) {
            val questionsInSubject = mockTestData?.data?.subjectQuestions
                ?.firstOrNull { it.subjectId == allSubjects[i].subjectId }
                ?.questions ?: emptyList()

            var totalQuestions = questionsInSubject.size

            // ✅ Adjust for sub-questions (subtracting 1 from total sub-question count)
            for (question in questionsInSubject) {
                val subCount = question.subQuestions?.size ?: 0
                if (subCount > 0) totalQuestions += (subCount - 1)
            }

            if (i < currentSubjectIndex) {
                questionNumberOffset += totalQuestions
            }
        }
    }


    private fun onSubjectSelected(subjectIndex: Int) {
        if (subjectIndex !in allSubjects.indices) return
        currentSubjectIndex = subjectIndex
        selectedSubjectId = allSubjects[subjectIndex].subjectId
        currentQuestionIndex = 0

        calculateQuestionOffsets()

        currentQuestions = mockTestData?.data?.subjectQuestions?.firstOrNull { it.subjectId == selectedSubjectId }?.questions ?: emptyList()
        updateQuestionDisplay()

        for (i in 0 until binding.subjectContainer.childCount) {
            val button = binding.subjectContainer.getChildAt(i) as TextView
            button.isSelected = (i == subjectIndex)
        }
    }

    private fun showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
        } else if (currentSubjectIndex > 0) {
            onSubjectSelected(currentSubjectIndex - 1)
            currentQuestionIndex = currentQuestions.size - 1
        }
        updateQuestionDisplay()
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex < currentQuestions.size - 1) {
            currentQuestionIndex++
        } else if (currentSubjectIndex < allSubjects.size - 1) {
            onSubjectSelected(currentSubjectIndex + 1)
            currentQuestionIndex = 0
        }
        updateQuestionDisplay()
    }

    private fun updateQuestionDisplay() {
        if (currentQuestions.isNotEmpty()) {
            val questionList = listOf(currentQuestions[currentQuestionIndex])
            // Calculate the effective main question number (start index) for this question.
            val effectiveNumber = getEffectiveQuestionNumberIndex()
            adapter.updateData(questionList, effectiveNumber)
        }

        binding.mcvPrevious.visibility = if (currentSubjectIndex == 0 && currentQuestionIndex == 0) View.GONE else View.VISIBLE
        binding.mcvNext.visibility = if (currentSubjectIndex == allSubjects.size - 1 && currentQuestionIndex == currentQuestions.size - 1) View.GONE else View.VISIBLE
    }

    /**
     * Computes the effective main question number based on:
     *   - The offset from previous subjects (questionNumberOffset), and
     *   - For each question in the current subject before the current one:
     *         • if the question has no sub‑questions, add 1;
     *         • if the question has sub‑questions, add the sub‑question count.
     * Then add 1 for the current question.
     */
    private fun getEffectiveQuestionNumberIndex(): Int {
        var effective = questionNumberOffset
        for (j in 0 until currentQuestionIndex) {
            val subCount = currentQuestions[j].subQuestions?.size ?: 0
            effective += if (subCount > 0) subCount else 1
        }
        effective += 1  // for the current question itself
        return effective
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


