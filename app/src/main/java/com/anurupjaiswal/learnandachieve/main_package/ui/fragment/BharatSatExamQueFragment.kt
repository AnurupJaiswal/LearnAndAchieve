package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.DialogSubmitConfirmationBinding
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatBinding
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatExamQueBinding
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatExamTicketBinding
import com.anurupjaiswal.learnandachieve.databinding.FragmentQuestionComparisonBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionComparisonAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionStatus
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionStatusAdapter
import com.anurupjaiswal.learnandachieve.model.BharatSatExamNumberOfQuestionsData
import com.anurupjaiswal.learnandachieve.model.NumberOfQuestionsData

import com.anurupjaiswal.learnandachieve.model.QuestionComparisonResponse
import com.anurupjaiswal.learnandachieve.model.QuestionComparisonSubject
import com.anurupjaiswal.learnandachieve.model.QuestionItem
import com.anurupjaiswal.learnandachieve.model.QuestionProgress
import com.anurupjaiswal.learnandachieve.model.SubmitBharatSetExamRequest
import com.anurupjaiswal.learnandachieve.model.SubmitMockTestRequest
import com.anurupjaiswal.learnandachieve.model.SubmitMockTestResponse
import com.anurupjaiswal.learnandachieve.model.SubmittedAnswer
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class BharatSatExamQueFragment : Fragment() {
    private var gridMapping = listOf<Pair<Int, Int>>()
    private var isDrawerOpen = false
    private var _binding: FragmentBharatSatExamQueBinding? = null
    private val binding get() = _binding!!

    private var countDownTimer: CountDownTimer? = null
    private lateinit var adapter: QuestionComparisonAdapter
    private var selectedSubjectId: String? = null
    private var mockTestData: QuestionComparisonResponse? = null
    private lateinit var questionStatusAdapter: QuestionStatusAdapter
    private val selectedAnswers = mutableMapOf<String, String>()
    private val viewedQuestions = mutableSetOf<String>()
    private var currentQuestionIndex = 0
    private var testStartTime: Long = 0L
    private var bharatSatExamId: String? = null
    private var eHallTicketId: String? = null
    private var currentSubjectIndex = 0
    private var allSubjects: List<QuestionComparisonSubject> = emptyList()
    private var currentQuestions: List<QuestionItem> = emptyList()
    private var questionNumberOffset = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBharatSatExamQueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bharatSatExamId = it.getString("bharatSatExamId", "")
        }

        arguments?.let {
            eHallTicketId = it.getString("eHallTicketId", "")
        }

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

        binding.llOpenDrawer.setOnClickListener {
            toggleDrawer()
        }

        binding.mcvSumit.setOnClickListener {
            val progress = getQuestionProgressAlt()
            Log.d(
                TAG,
                "Attempted: ${progress.attempted}, Not Attempted: ${progress.total - progress.attempted}, Total: ${progress.total}"
            )
            showSubmitConfirmationDialog(progress)
        }

        binding.tvAllQue.setOnClickListener {
            toggleDrawer()
        }

        binding.mcvPrevious.setOnClickListener { showPreviousQuestion() }
        binding.mcvNext.setOnClickListener { showNextQuestion() }

        Log.e(TAG, "onViewCreated:${bharatSatExamId}")
        bharatSatExamId?.let { fetchQuestions(it) }

        binding.rcvQuestionComparison.adapter = adapter
        binding.rcvQuestionGrid.layoutManager = GridLayoutManager(requireContext(), 8)
        questionStatusAdapter = QuestionStatusAdapter(emptyList()) { position ->
            navigateToQuestion(position)
        }
        binding.rcvQuestionGrid.adapter = questionStatusAdapter

        binding.llDrawer.visibility = View.GONE


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

    private fun fetchQuestions(bharatSatExamId: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.llMain.visibility = View.GONE

        val apiService = RetrofitClient.client
        val token = Utils.GetSession().token
        apiService.getBharatSatQuestions("Bearer $token", bharatSatExamId)
            .enqueue(object : Callback<QuestionComparisonResponse> {
                override fun onResponse(call: Call<QuestionComparisonResponse>, response: Response<QuestionComparisonResponse>) {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            response.body()?.let { dataResponse ->
                                // Extract the "Date" header from the response.
                                val dateHeader = response.headers()["Date"]
                                if (dateHeader != null) {
                                    // Example: "Sat, 22 Feb 2025 05:51:52 GMT"
                                    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                                    testStartTime = try {
                                        dateFormat.parse(dateHeader)?.time ?: System.currentTimeMillis()

                                    } catch (e: Exception) {
                                        System.currentTimeMillis()
                                    }
                                } else {
                                    testStartTime = System.currentTimeMillis()
                                }

                                mockTestData = dataResponse
                                allSubjects = dataResponse.data.subjects
                                setupSubjectTabs(allSubjects)

                                // Use exam start and end times from response to start countdown.
                                startExamCountdown(dataResponse.data.examStartTime, dataResponse.data.examEndTime)

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
                            Utils.E("fetchbharatSatExamQuestions Error: ${response.code()} - ${response.message()}")
                            Utils.T(requireContext(), "Error: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionComparisonResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.llMain.visibility = View.VISIBLE
                    Utils.E("fetchbharatSatExamQuestions API Call Failed: ${t.message}")
                    Utils.T(requireContext(), t.message ?: "Request failed. Try again later")
                }
            })
    }

    private fun setupSubjectTabs(subjects: List<QuestionComparisonSubject>) {
        val subjectContainer = binding.subjectContainer
        subjectContainer.removeAllViews()
        for ((index, subject) in subjects.withIndex()) {
            val button = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_subject_button, subjectContainer, false) as TextView
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
            translationX =
                Resources.getSystem().displayMetrics.widthPixels.toFloat() // Start off-screen
            animate().translationX(0f).setDuration(300).start() // Slide in
        }
        binding.llOpenDrawer.apply {
            visibility = View.VISIBLE
            translationX =
                Resources.getSystem().displayMetrics.widthPixels.toFloat() // Start off-screen
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
        countDownTimer?.cancel()
        _binding = null
    }

    private fun submitBharatSatExam() {
        // Validate required IDs.
        if (bharatSatExamId.isNullOrEmpty() || eHallTicketId.isNullOrEmpty()) {
            E("bharatSatExam: Missing required IDs (bharatSatExamId or eHallTicketId)")
            return
        }

        val token = Utils.GetSession().token
        val submittedAnswers = mutableListOf<SubmittedAnswer>()

        // Loop through all subjects' questions from the fetched bharatSatExamData.
        mockTestData?.data?.subjectQuestions?.forEach { subjectQuestions ->
            subjectQuestions.questions.forEach { question ->
                if (question.subQuestions.isEmpty()) {
                    val answerStr = selectedAnswers[question.questionId] ?: ""
                    val selectedOption = if (answerStr.isNotEmpty()) answerStr.toIntOrNull() ?: -1 else -1
                    submittedAnswers.add(
                        SubmittedAnswer(
                            question_id = question.questionId,
                            sub_question_id = null,
                            selectedOption = selectedOption,
                            typeOfQuestion = question.typeOfQuestion
                        )
                    )
                } else {
                    question.subQuestions.forEach { subQuestion ->
                        val answerStr = selectedAnswers[subQuestion.subQuestionId] ?: ""
                        val selectedOption = if (answerStr.isNotEmpty()) answerStr.toIntOrNull() ?: -1 else -1
                        submittedAnswers.add(
                            SubmittedAnswer(
                                question_id = question.questionId,
                                sub_question_id = subQuestion.subQuestionId,
                                selectedOption = selectedOption,
                                typeOfQuestion = question.typeOfQuestion
                            )
                        )
                    }
                }
            }
        }

        // Build the numberOfQuestionsData list for all subjects.
        val numberOfQuestionsData = allSubjects.map { subject ->
            val questions = mockTestData?.data?.subjectQuestions
                ?.firstOrNull { it.subjectId == subject.subjectId }
                ?.questions ?: emptyList()
            BharatSatExamNumberOfQuestionsData(
                subjectId = subject.subjectId,
                numberOfQuestionsBank = questions.size,
                numberOfQuestionsBharatSat = questions.size
            )
        }

        // Calculate elapsed time as the difference between examEndTime and testStartTime.
        // Parse examEndTime from the API.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val examEndTimestamp = dateFormat.parse(mockTestData?.data?.examEndTime ?: "")?.time
            ?: System.currentTimeMillis()
        val elapsedMillis = examEndTimestamp - testStartTime
        val elapsedTimeString = String.format(
            "%02d:%02d:%02d",
            elapsedMillis / (3600 * 1000),
            (elapsedMillis % (3600 * 1000)) / (60 * 1000),
            (elapsedMillis % (60 * 1000)) / 1000
        )

        // Format times: startTime and endTime in "h:mm:ss a" format,
        // and submitted_date in "d/M/yyyy, h:mm:ss a" format.
        val timeFormatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("d/M/yyyy, h:mm:ss a", Locale.getDefault())
        val startTimeString = timeFormatter.format(Date(testStartTime)).lowercase()
        val endTimeString = timeFormatter.format(Date(examEndTimestamp)).lowercase()
        val submittedDate = dateFormatter.format(Date(examEndTimestamp)).lowercase()

        val request = SubmitBharatSetExamRequest(
            bharatSatExamId = bharatSatExamId!!,
            eHallTicketId = eHallTicketId!!,
            startTime = startTimeString,
            endTime = endTimeString,
            submitted_date = submittedDate,
            submittedTime = elapsedTimeString,
            submittedAnswers = submittedAnswers,
            numberOfQuestionsData = numberOfQuestionsData
        )

        Utils.toggleProgressBarAndText(true, binding.loading, binding.tvSumit, binding.root)

        RetrofitClient.client.submitbharatSatExam("Bearer $token", request)
            .enqueue(object : Callback<SubmitMockTestResponse> {
                override fun onResponse(
                    call: Call<SubmitMockTestResponse>,
                    response: Response<SubmitMockTestResponse>
                ) {
                    Utils.toggleProgressBarAndText(false, binding.loading, binding.tvSumit, binding.root)
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            val navController = findNavController()
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.home, inclusive = false)
                                .build()
                            navController.navigate(R.id.BharatSatSubmitSuccessFragment, null, navOptions)

                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.E("bharatSatExam UNAUTHORIZED: ${response.message()}")
                            Utils.UnAuthorizationToken(requireContext())
                        }
                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                Utils.E("bharatSatExam BAD_REQUEST: $errorMessage")
                            }
                        }
                        else -> {
                            Utils.E("bharatSatExam Error: ${response.code()} - ${response.errorBody()?.string()}")
                            Utils.T(requireContext(), "Submission failed. Please try again.")
                        }
                    }
                }

                override fun onFailure(call: Call<SubmitMockTestResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Utils.E("bharatSatExam Failure: ${t.message}")
                    Utils.T(requireContext(), "Network error. Please try again.")
                }
            })
    }




    private fun getQuestionProgressAlt(): QuestionProgress {
        var totalCount = 0
        var attemptedCount = 0

        allSubjects.forEach { subject ->
            val subjectQuestions = mockTestData?.data?.subjectQuestions
                ?.firstOrNull { it.subjectId == subject.subjectId }
                ?.questions ?: emptyList()

            subjectQuestions.forEach { question ->

                if (question.subQuestions.isNullOrEmpty()) {
                    totalCount++
                    if (selectedAnswers.containsKey(question.questionId)) {
                        attemptedCount++
                    }
                } else {
                    // If there are sub-questions, count each one individually.
                    question.subQuestions.forEach { subQuestion ->
                        totalCount++
                        if (selectedAnswers.containsKey(subQuestion.subQuestionId)) {
                            attemptedCount++
                        }
                    }
                }
            }
        }
        return QuestionProgress(attempted = attemptedCount, total = totalCount)
    }

    private fun showSubmitConfirmationDialog(progress: QuestionProgress) {
        // Inflate the custom dialog layout using view binding
        val dialogBinding = DialogSubmitConfirmationBinding.inflate(layoutInflater)

        // Set the progress values in the dialog
        dialogBinding.tvAttemptedQuestions.text = "No. of attempted questions: ${progress.attempted}"
        val notAttempted = progress.total - progress.attempted
        dialogBinding.tvNotAttemptedQuestions.text = "Not attempted questions: $notAttempted"
        dialogBinding.tvTimer.text = binding.tvTimer.text

        // Create a Dialog instance
        val dialog = Dialog(requireContext())
        // Remove default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Set the custom view for the dialog
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set click listeners
        dialogBinding.mcvCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.mcvOk.setOnClickListener {
            dialog.dismiss()
            submitBharatSatExam()
        }

        // Display the dialog
        dialog.show()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }


    private fun startExamCountdown(examStartTime: String, examEndTime: String) {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Adjust if needed
        val startTimestamp = dateFormat.parse(examStartTime)?.time ?: System.currentTimeMillis()
        val endTimestamp = dateFormat.parse(examEndTime)?.time ?: System.currentTimeMillis()
        val currentTime = System.currentTimeMillis()
        Log.d(TAG, "startTimestamp: $startTimestamp, endTimestamp: $endTimestamp, currentTime Device Time : $currentTime")
        if (currentTime < startTimestamp) {
            binding.tvTimer.text = "Exam not started yet"
            return
        }
        val timeLeft = endTimestamp - testStartTime
        Log.d(TAG, "startTimestamp: $startTimestamp, endTimestamp: $endTimestamp, testStartTime : $testStartTime, timeLeft : $timeLeft")

        if (timeLeft <= 0) {
            binding.tvTimer.text = "00hr 00min 00secs left"
            return
        }
        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val hours = (millisUntilFinished / (1000 * 3600))
                updateTimerText(hours, minutes, seconds)
            }
            override fun onFinish() {
                binding.tvTimer.text = "00hr 00min 00secs left"
            }
        }.start()
    }
    private fun updateTimerText(hours: Long, minutes: Long, seconds: Long) {
        // Format string e.g. "01hr 22min 46secs left"

        val timeString = String.format("%02dhr %02dmin %02dsecs left", hours, minutes, seconds)
        val spannable = SpannableString(timeString)

        // Bold numeric parts:
        // Bold hours digits (first 2 characters)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Bold minutes digits: find the start index after "hr " (which is at index 4)
        val minStart = timeString.indexOf(" ", 0) + 1 // index after first space
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            minStart, minStart + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Bold seconds digits: find the start index of seconds digits.
        // We'll use lastIndexOf for "secs" and subtract 2.
        val secsIndex = timeString.indexOf("secs")
        val secDigitsStart = secsIndex - 2
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            secDigitsStart, secDigitsStart + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Italicize unit labels:
        // Italicize "hr"
        val hrIndex = timeString.indexOf("hr")
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            hrIndex, hrIndex + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Italicize "min"
        val minLabelIndex = timeString.indexOf("min")
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            minLabelIndex, minLabelIndex + 3,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Italicize "secs"
        val secsLabelIndex = timeString.indexOf("secs")
        spannable.setSpan(
            StyleSpan(Typeface.ITALIC),
            secsLabelIndex, secsLabelIndex + 4,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Finally update the TextView.
        binding.tvTimer.text = spannable
    }
}