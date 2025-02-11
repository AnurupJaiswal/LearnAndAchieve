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
    }

    private fun fetchMockTestQuestions(mockTestId: String) {
        val apiService = RetrofitClient.client
        val token = Utils.GetSession().token

        apiService.getMockTestQuestions("Bearer $token", mockTestId).enqueue(object : Callback<QuestionComparisonResponse> {
            override fun onResponse(call: Call<QuestionComparisonResponse>, response: Response<QuestionComparisonResponse>) {
                if (response.isSuccessful) {
                    mockTestData = response.body()
                    mockTestData?.let { data ->
                        setupSubjectTabs(data.data.subjects)
                        val durationInMinutes = data.data.durationInMinutes ?: 0
                        Utils.startCountdownTimer(durationInMinutes, binding.tvTimer)
                        // Automatically select the first subject
                        if (data.data.subjects.isNotEmpty()) {
                            val firstSubject = data.data.subjects.first()
                            onSubjectSelected(firstSubject.subjectId)
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
                onSubjectSelected(subject.subjectId)
            }
            subjectContainer.addView(button)

            // Highlight the first subject by default
            if (index == 0) {
                button.isSelected = true
            }
        }
    }

    private fun onSubjectSelected(subjectId: String) {
        selectedSubjectId = subjectId

        val allSubjects = mockTestData?.data?.subjects ?: emptyList()
        val selectedSubjectIndex = allSubjects.indexOfFirst { it.subjectId == subjectId }

        // Calculate the start index based on previous subjects' question counts
        val newStartIndex = allSubjects.take(selectedSubjectIndex).sumOf { subject ->
            mockTestData?.data?.subjectQuestions?.firstOrNull { it.subjectId == subject.subjectId }?.questions?.size ?: 0
        } + 1

        val questions = mockTestData?.data?.subjectQuestions?.firstOrNull { it.subjectId == subjectId }?.questions ?: emptyList()
        adapter.updateData(questions, newStartIndex)

        // Highlight the selected subject button
        for (i in 0 until binding.subjectContainer.childCount) {
            val button = binding.subjectContainer.getChildAt(i) as TextView
            val subject = mockTestData?.data?.subjects?.getOrNull(i)
            button.isSelected = (subject?.subjectId == subjectId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}