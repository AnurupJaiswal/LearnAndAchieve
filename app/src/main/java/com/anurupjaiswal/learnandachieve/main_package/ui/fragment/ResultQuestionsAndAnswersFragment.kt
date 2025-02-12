package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentResultQuestionsAndAnswersBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ResultQuestionsAndAnswersAdapter
import com.anurupjaiswal.learnandachieve.model.PerformanceSubject
import com.anurupjaiswal.learnandachieve.model.PerformanceSummaryResponse
import com.anurupjaiswal.learnandachieve.model.Question
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultQuestionsAndAnswersFragment : Fragment() {

    private var _binding: FragmentResultQuestionsAndAnswersBinding? = null
    private val binding get() = _binding!!

    // IDs passed from previous fragment.
    private var mockTestId: String? = null
    private var mockTestSubmissionsId: String? = null

    // Overall data fetched initially.
    private var allQuestions: List<Question> = emptyList()
    private var allSubjects: List<PerformanceSubject> = emptyList()

    // For navigation within a subject.
    private var currentQuestions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    // currentStartIndex is the starting number for the current subject (computed as main question count from previous subjects + 1)
    private var currentStartIndex = 1

    // Track current subject index.
    private var currentSubjectIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mockTestId = it.getString("mockTest_id")
            mockTestSubmissionsId = it.getString("mockTestSubmissions_id")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultQuestionsAndAnswersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvQuestions.layoutManager = LinearLayoutManager(requireContext())

        // Set up navigation buttons.
        binding.mcvPrevious.setOnClickListener { showPreviousQuestion() }
        binding.mcvNext.setOnClickListener { showNextQuestion() }

        if (mockTestId.isNullOrEmpty() || mockTestSubmissionsId.isNullOrEmpty()) {
          E("Invalid parameters")
            return
        }
        fetchOverallPerformanceData()
    }

    private fun fetchOverallPerformanceData() {
        binding.progressBar.visibility = View.VISIBLE
        val token = Utils.GetSession().token
        RetrofitClient.client.getPerformanceSummary("Bearer $token", mockTestId!!, mockTestSubmissionsId!!)
            .enqueue(object : Callback<PerformanceSummaryResponse> {
                override fun onResponse(call: Call<PerformanceSummaryResponse>, response: Response<PerformanceSummaryResponse>) {
                    binding.progressBar.visibility = View.GONE
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            response.body()?.let { performanceResponse ->
                                val data = performanceResponse.data
                                allQuestions = data.questions
                                allSubjects = data.subjects

                                setupSubjectContainer(allSubjects)
                                if (allSubjects.isNotEmpty()) {
                                    currentSubjectIndex = 0
                                    onSubjectSelected(currentSubjectIndex)
                                }
                            }
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.E("fetchOverallPerformanceData UNAUTHORIZED: ${response.message()}")
                            Utils.UnAuthorizationToken(requireContext())
                        }
                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                Utils.E("fetchOverallPerformanceData BAD_REQUEST: $errorMessage")
                                Utils.T(requireContext(), errorMessage)
                            }
                        }
                        else -> {
                            E("fetchOverallPerformanceData Error: ${response.code()} - ${response.errorBody()?.string()}")
                            Utils.T(requireContext(), "Error fetching data")
                        }
                    }
                }

                override fun onFailure(call: Call<PerformanceSummaryResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Utils.E("Network error: ${t.localizedMessage}")
                }
            })
    }

    private fun setupSubjectContainer(subjects: List<PerformanceSubject>) {
        binding.subjectContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        for ((index, subject) in subjects.withIndex()) {
            val subjectView = inflater.inflate(R.layout.item_subject_button, binding.subjectContainer, false) as TextView
            subjectView.text = subject.subjectName
            subjectView.isClickable = true
            subjectView.isFocusable = true
            subjectView.setOnClickListener {
                currentSubjectIndex = index
                onSubjectSelected(index)
            }
            binding.subjectContainer.addView(subjectView)
        }
    }

    /**
     * Compute the numbering offset for the selected subject by summing the number of main questions
     * (ignoring sub-questions) in all preceding subjects.
     */
    private fun computeOffsetForSubject(subjectIndex: Int): Int {
        // Here we assume each subject’s totalQuestions is the count of main questions (not including sub-questions).
        var offset = 0
        for (i in 0 until subjectIndex) {
            offset += allSubjects[i].totalQuestions
        }
        return offset
    }

    /**
     * Computes the display number for the main question at [index] in currentQuestions.
     * It starts at currentStartIndex and adds:
     *   - the main question count (which is just [index])
     *   - plus the extra numbers for each previous main question that had sub‑questions.
     *
     * The extra for a question with sub‑questions is (number of sub‑questions – 1)
     * (since the main question itself already occupies one number).
     */
    private fun computeDisplayNumber(index: Int): Int {
        var number = currentStartIndex + index
        for (i in 0 until index) {
            val subCount = currentQuestions[i].subQuestions?.size ?: 0
            if (subCount > 0) {
                number += (subCount - 1)
            }
        }
        return number
    }

    private fun onSubjectSelected(subjectIndex: Int) {
        if (subjectIndex !in allSubjects.indices) return
        val selectedSubject = allSubjects[subjectIndex]
        val offset = computeOffsetForSubject(subjectIndex)
        // Here, currentStartIndex is computed from the count of main questions in previous subjects.
        currentStartIndex = offset + 1

        binding.progressBar.visibility = View.VISIBLE
        val token = Utils.GetSession().token
        RetrofitClient.client.getPerformanceSummary(
            "Bearer $token",
            mockTestId!!,
            mockTestSubmissionsId!!,
            subjectId = selectedSubject.subjectId
        ).enqueue(object : Callback<PerformanceSummaryResponse> {
            override fun onResponse(call: Call<PerformanceSummaryResponse>, response: Response<PerformanceSummaryResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val subjectQuestions = response.body()!!.data.questions
                    currentQuestions = subjectQuestions
                    currentQuestionIndex = 0
                    updateQuestionDisplay()
                    highlightSelectedSubject(subjectIndex)
                } else {
                    E("Error fetching subject data")
                }
            }
            override fun onFailure(call: Call<PerformanceSummaryResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                E("Network error: ${t.localizedMessage}")
            }
        })
    }

    /**
     * Update the displayed question using the adapter. Only one main question is shown at a time.
     * The header will use the continuous numbering computed by computeDisplayNumber().
     */
    private fun updateQuestionDisplay() {
        if (currentQuestions.isNotEmpty() && currentQuestionIndex in currentQuestions.indices) {
            val question = currentQuestions[currentQuestionIndex]
            val displayNumber = computeDisplayNumber(currentQuestionIndex)
            binding.rvQuestions.adapter = ResultQuestionsAndAnswersAdapter(
                listOf(question),
                displayNumber
            )
        }
        binding.mcvPrevious.visibility = if (currentQuestionIndex == 0 && currentSubjectIndex == 0) View.GONE else View.VISIBLE
        binding.mcvNext.visibility = if (currentQuestionIndex == currentQuestions.size - 1 && currentSubjectIndex == allSubjects.size - 1) View.GONE else View.VISIBLE
    }

    private fun showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            updateQuestionDisplay()
        } else if (currentSubjectIndex > 0) {
            currentSubjectIndex--
            onSubjectSelected(currentSubjectIndex)
            binding.rvQuestions.postDelayed({
                currentQuestionIndex = currentQuestions.size - 1
                updateQuestionDisplay()
            }, 300)
        }
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex < currentQuestions.size - 1) {
            currentQuestionIndex++
            updateQuestionDisplay()
        } else if (currentSubjectIndex < allSubjects.size - 1) {
            currentSubjectIndex++
            onSubjectSelected(currentSubjectIndex)
            binding.rvQuestions.postDelayed({
                currentQuestionIndex = 0
                updateQuestionDisplay()
            }, 300)
        }
    }

    private fun highlightSelectedSubject(selectedIndex: Int) {
        for (i in 0 until binding.subjectContainer.childCount) {
            val tv = binding.subjectContainer.getChildAt(i) as TextView
            if (i == selectedIndex) {
                tv.background = ContextCompat.getDrawable(requireContext(), R.drawable.subject_selected_background)
                tv.setTextColor(Color.WHITE)
            } else {
                tv.background = ContextCompat.getDrawable(requireContext(), R.drawable.subject_unselected_background)
                tv.setTextColor(Color.BLACK)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
