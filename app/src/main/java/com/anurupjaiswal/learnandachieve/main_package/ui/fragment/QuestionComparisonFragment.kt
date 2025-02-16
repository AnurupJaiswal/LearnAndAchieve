package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentQuestionComparisonBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionComparisonAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionStatus
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionStatusAdapter
import com.anurupjaiswal.learnandachieve.model.NumberOfQuestionsData

import com.anurupjaiswal.learnandachieve.model.QuestionComparisonResponse
import com.anurupjaiswal.learnandachieve.model.QuestionComparisonSubject
import com.anurupjaiswal.learnandachieve.model.QuestionItem
import com.anurupjaiswal.learnandachieve.model.SubmitMockTestRequest
import com.anurupjaiswal.learnandachieve.model.SubmitMockTestResponse
import com.anurupjaiswal.learnandachieve.model.SubmittedAnswer
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class QuestionComparisonFragment : Fragment() {
    private var gridMapping = listOf<Pair<Int, Int>>()
    private var isDrawerOpen = false
    private var _binding: FragmentQuestionComparisonBinding? = null
    private val binding get() = _binding!!
    private var mockTestId: String? = null
    private var package_id: String? = null
    private var order_id: String? = null
    private lateinit var adapter: QuestionComparisonAdapter
    private var selectedSubjectId: String? = null
    private var mockTestData: QuestionComparisonResponse? = null
    private lateinit var questionStatusAdapter: QuestionStatusAdapter
    private val selectedAnswers = mutableMapOf<String, String>()
    private val viewedQuestions = mutableSetOf<String>()
    private var currentQuestionIndex = 0
    private var testStartTime: Long = 0L

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
        package_id = arguments?.getString("package_id")
        order_id = arguments?.getString("order_id")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvQuestionComparison.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionComparisonAdapter(
            emptyList(),
            startIndex = 1,
            { questionId, answer ->
                if (answer.trim().isEmpty()) {
                    selectedAnswers.remove(questionId)
                } else {
                    selectedAnswers[questionId] = answer
                }
                updateQuestionDisplay()
            },
            {
                if (isDrawerOpen) {
                    closeDrawerWithAnimation()
                }
            }
        )

        binding.rcvQuestionComparison.adapter = adapter



        binding.rcvQuestionComparison.adapter = adapter
        binding.rcvQuestionGrid.layoutManager = GridLayoutManager(requireContext(), 8)
        questionStatusAdapter = QuestionStatusAdapter(emptyList()) { position ->
            navigateToQuestion(position)
        }
        binding.rcvQuestionGrid.adapter = questionStatusAdapter

         binding.llDrawer.visibility =  View.GONE
        binding.llOpenDrawer.setOnClickListener {
            toggleDrawer()
        }

        binding.mcvSumit.setOnClickListener {
            submitMockTest()
        }
        binding.tvAllQue.setOnClickListener {
            toggleDrawer()
        }



        mockTestId?.let {
            fetchMockTestQuestions(it)
        } ?: Log.e("QuestionComparison", "MockTest ID is null!")

        binding.mcvPrevious.setOnClickListener { showPreviousQuestion() }
        binding.mcvNext.setOnClickListener { showNextQuestion() }

        binding.root.setOnTouchListener { _, event ->
            if (isDrawerOpen && event.action == MotionEvent.ACTION_DOWN) {
                val outRect = Rect()
                binding.llDrawer.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    closeDrawerWithAnimation()
                    return@setOnTouchListener true
                }
            }
            false
        }

    }
    private fun fetchMockTestQuestions(mockTestId: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.llMain.visibility = View.GONE

        val apiService = RetrofitClient.client
        val token = Utils.GetSession().token
        apiService.getMockTestQuestions("Bearer $token", mockTestId).enqueue(object : Callback<QuestionComparisonResponse> {
            override fun onResponse(call: Call<QuestionComparisonResponse>, response: Response<QuestionComparisonResponse>) {
                when (response.code()) {
                    StatusCodeConstant.OK -> {
                        response.body()?.let { dataResponse ->
                            mockTestData = dataResponse
                            allSubjects = dataResponse.data.subjects
                            setupSubjectTabs(allSubjects)
                            val durationInMinutes = dataResponse.data.durationInMinutes ?: 0
                            Utils.startCountdownTimer(durationInMinutes, binding.tvTimer)
                            testStartTime = System.currentTimeMillis()
                            if (allSubjects.isNotEmpty()) {
                                calculateQuestionOffsets()
                                onSubjectSelected(0) // Select first subject by default
                                binding.progressBar.visibility = View.GONE
                                binding.llMain.visibility = View.VISIBLE
                            }
                        }
                    }
                    StatusCodeConstant.UNAUTHORIZED -> {
                        binding.progressBar.visibility = View.GONE
                        binding.llMain.visibility = View.VISIBLE
                        Utils.E("fetchMockTestQuestions UNAUTHORIZED: ${response.message()}")
                        Utils.UnAuthorizationToken(requireContext())
                    }
                    StatusCodeConstant.BAD_REQUEST -> {
                        binding.progressBar.visibility = View.GONE
                        binding.llMain.visibility = View.VISIBLE
                        response.errorBody()?.let { errorBody ->
                            val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val errorMessage = apiError.error ?: "Bad Request Error"
                            Utils.E("fetchMockTestQuestions BAD_REQUEST: $errorMessage")
                            Utils.T(requireContext(), errorMessage)
                        }
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.llMain.visibility = View.VISIBLE
                        Utils.E("fetchMockTestQuestions Error: ${response.code()} - ${response.message()}")
                        Utils.T(requireContext(), "Error: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<QuestionComparisonResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.llMain.visibility = View.VISIBLE
                Utils.E("fetchMockTestQuestions API Call Failed: ${t.message}")
                Utils.T(requireContext(), t.message ?: "Request failed. Try again later")
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
            // Adjust for sub-questions (subtracting 1 from total sub-question count)
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
        val questions = mockTestData?.data?.subjectQuestions
            ?.firstOrNull { it.subjectId == selectedSubjectId }
            ?.questions ?: emptyList()

        if (questions.isEmpty()) {
          E("QuestionComparison No questions found for subject: $selectedSubjectId")
            return
        }

        currentQuestions = questions
        updateQuestionDisplay()
        for (i in 0 until binding.subjectContainer.childCount) {
            val button = binding.subjectContainer.getChildAt(i) as TextView
            button.isSelected = (i == subjectIndex)
        }
    }
        private fun toggleDrawer() {
            if (isDrawerOpen) {
                closeDrawerWithAnimation()
            } else {
                openDrawerWithAnimation()
            }
        }
        private fun openDrawerWithAnimation() {
            binding.llDrawer.apply {
                visibility = View.VISIBLE
                translationX = Resources.getSystem().displayMetrics.widthPixels.toFloat() // Start off-screen
                animate().translationX(0f).setDuration(300).start() // Slide in
            }
            binding.llOpenDrawer.apply {
                visibility = View.VISIBLE
                translationX = Resources.getSystem().displayMetrics.widthPixels.toFloat() // Start off-screen
                animate().translationX(0f).setDuration(300).start() // Slide in
            }
            binding.ivArrowQue.animate().rotation(180f).setDuration(300).start()
            isDrawerOpen = true
        }
        private fun closeDrawerWithAnimation() {
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()

            binding.llDrawer.animate().translationX(screenWidth).setDuration(300)
                .withEndAction { binding.llDrawer.visibility = View.GONE }
                .start()
            binding.llOpenDrawer.animate().translationX(screenWidth).setDuration(300)
                .withEndAction {
                    binding.llOpenDrawer.translationX = 0f  // Reset position
                }.start()
            binding.ivArrowQue.animate().rotation(0f).setDuration(300).start()

            isDrawerOpen = false
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
                // Get the current question being displayed.
                val currentQuestion = currentQuestions[currentQuestionIndex]

                // Mark the current question as viewed.
                // (Even if it was previously viewed, adding again has no effect.)
                viewedQuestions.add(currentQuestion.questionId)

                // If there are sub‑questions, mark each sub‑question as viewed.
                currentQuestion.subQuestions?.forEach { subQuestion ->
                    viewedQuestions.add(subQuestion.subQuestionId)
                }

                // Update the question adapter with the current question.
                // The effective number (question numbering) is calculated separately.
                val effectiveNumber = getEffectiveQuestionNumberIndex()
                adapter.updateData(listOf(currentQuestion), effectiveNumber)
            }

            // Build the grid statuses using the shared selectedAnswers and viewedQuestions.
            // If a question (or sub‑question) is answered, it gets the ANSWERED status.
            // Otherwise, if it’s been viewed, it’s NOT_ANSWERED.
            // If not even viewed, it remains NOT_VIEWED.
            val statuses = mutableListOf<QuestionStatus>()
            val mapping = mutableListOf<Pair<Int, Int>>()

            allSubjects.forEachIndexed { subjectIndex, subject ->
                // Find the subject's questions.
                val subjectQuestions = mockTestData?.data?.subjectQuestions
                    ?.firstOrNull { it.subjectId == subject.subjectId }
                    ?.questions ?: emptyList()

                subjectQuestions.forEachIndexed { qIndex, question ->
                    if (question.subQuestions.isNullOrEmpty()) {
                        // For questions without sub‑questions:
                        val status = when {
                            selectedAnswers.containsKey(question.questionId) -> QuestionStatus.ANSWERED
                            viewedQuestions.contains(question.questionId) -> QuestionStatus.NOT_ANSWERED
                            else -> QuestionStatus.NOT_VIEWED
                        }
                        statuses.add(status)
                        mapping.add(Pair(subjectIndex, qIndex))
                    } else {
                     
                        question.subQuestions!!.forEach { subQuestion ->
                            val status = when {
                                selectedAnswers.containsKey(subQuestion.subQuestionId) -> QuestionStatus.ANSWERED
                                viewedQuestions.contains(subQuestion.subQuestionId) -> QuestionStatus.NOT_ANSWERED
                                else -> QuestionStatus.NOT_VIEWED
                            }
                            statuses.add(status)
                            // Map the sub‑question to the main question index.
                            mapping.add(Pair(subjectIndex, qIndex))
                        }
                    }
                }
            }

            gridMapping = mapping.toList()
            questionStatusAdapter.updateData(statuses)

            // Update the navigation button visibility.
            binding.mcvPrevious.visibility =
                if (currentSubjectIndex == 0 && currentQuestionIndex == 0) View.GONE else View.VISIBLE
            binding.mcvNext.visibility =
                if (currentSubjectIndex == allSubjects.size - 1 && currentQuestionIndex == currentQuestions.size - 1) View.GONE else View.VISIBLE
        }



        private fun getEffectiveQuestionNumberIndex(): Int {
        var effective = questionNumberOffset
        for (j in 0 until currentQuestionIndex) {
            val subCount = currentQuestions[j].subQuestions?.size ?: 0
            effective += if (subCount > 0) subCount else 1
        }
        return effective + 1 // Add 1 for the current question
    }
    private fun navigateToQuestion(position: Int) {
        if (position < 0 || position >= gridMapping.size) return  // safety check
        val (subjectIndex, mainQuestionIndex) = gridMapping[position]
        // Switch to the tapped subject and question.
        onSubjectSelected(subjectIndex)
        currentQuestionIndex = mainQuestionIndex
        updateQuestionDisplay()
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

    private fun submitMockTest() {
        // Validate required IDs.
        if (mockTestId.isNullOrEmpty() || package_id.isNullOrEmpty() || order_id.isNullOrEmpty()) {
            E("SubmitMockTes Missing required IDs")
            return
        }

        val token = Utils.GetSession().token
        val submittedAnswers = mutableListOf<SubmittedAnswer>()

        // Loop through all subjects' questions from the fetched mockTestData.
        mockTestData?.data?.subjectQuestions?.forEach { subjectQuestions ->
            subjectQuestions.questions.forEach { question ->
                if (question.subQuestions.isEmpty()) {
                    // For main questions without sub‑questions:
                    // If an answer was given, use it; otherwise default to -1.
                    val answerStr = selectedAnswers[question.questionId] ?: ""
                    val selectedOption = if (answerStr.isNotEmpty()) {
                        answerStr.toIntOrNull() ?: -1
                    } else {
                        -1
                    }
                    submittedAnswers.add(
                        SubmittedAnswer(
                            question_id = question.questionId,
                            sub_question_id = null,
                            selectedOption = selectedOption,
                            typeOfQuestion = question.typeOfQuestion
                        )
                    )
                } else {
                    // For questions with sub‑questions, iterate over each sub‑question.
                    question.subQuestions.forEach { subQuestion ->
                        val answerStr = selectedAnswers[subQuestion.subQuestionId] ?: ""
                        val selectedOption = if (answerStr.isNotEmpty()) {
                            answerStr.toIntOrNull() ?: -1
                        } else {
                            -1
                        }
                        submittedAnswers.add(
                            SubmittedAnswer(
                                question_id = question.questionId,
                                sub_question_id = subQuestion.subQuestionId,
                                selectedOption = selectedOption,
                                // Using the main question's type; adjust if needed.
                                typeOfQuestion = question.typeOfQuestion
                            )
                        )
                    }
                }
            }
        }

        // Build the numberOfQuestionsData list from your subjects.
        val numberOfQuestionsData = allSubjects.map { subject ->
            // Find the questions for this subject.
            val questions = mockTestData?.data?.subjectQuestions
                ?.firstOrNull { it.subjectId == subject.subjectId }
                ?.questions ?: emptyList()
            NumberOfQuestionsData(
                subjectId = subject.subjectId,
                numberOfQuestions = questions.size
            )
        }

        // Calculate elapsed time since test started.
        val testEndTime = System.currentTimeMillis()
        val elapsedMillis = testEndTime - testStartTime

// Compute hours, minutes, and seconds.
        val hours = elapsedMillis / (3600 * 1000)
        val minutes = (elapsedMillis % (3600 * 1000)) / (60 * 1000)
        val seconds = (elapsedMillis % (60 * 1000)) / 1000

        val elapsedTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)


        val request = SubmitMockTestRequest(
            mockTest_id = mockTestId!!,
            package_id = package_id!!,
            order_id = order_id!!,
            submittedAnswers = submittedAnswers,
            numberOfQuestionsData = numberOfQuestionsData,
            submittedTime = elapsedTimeString
        )

        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.client.submitMockTest("Bearer $token", request)
            .enqueue(object : Callback<SubmitMockTestResponse> {
                override fun onResponse(
                    call: Call<SubmitMockTestResponse>,
                    response: Response<SubmitMockTestResponse>
                ) {
                    binding.progressBar.visibility = View.GONE

                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            Utils.T(requireContext(), "Mock Test submitted successfully!")
                            findNavController().popBackStack()

                        }

                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.E("submitMockTest UNAUTHORIZED: ${response.message()}")
                            Utils.UnAuthorizationToken(requireContext())
                        }

                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                Utils.E("submitMockTest BAD_REQUEST: $errorMessage")
                            }
                        }

                        else -> {
                            Utils.E("submitMockTest Error: ${response.code()} - ${response.errorBody()?.string()}")
                            Utils.T(requireContext(), "Submission failed. Please try again.")
                        }
                    }
                }

                override fun onFailure(call: Call<SubmitMockTestResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Utils.E("submitMockTest Failure: ${t.message}")
                    Utils.T(requireContext(), "Network error. Please try again.")
                }
            })

    }

}


