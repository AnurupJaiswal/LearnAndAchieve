package com.anurupjaiswal.learnandachieve.model

data class PerformanceSummaryResponse(
    val message: String,
    val data: PerformanceData
)

data class PerformanceData(
    val mockTestId: String,
    val mockTestName: String,
    val mockTestSubmissionsId: String,
    val submittedTime: String,
    val subjects: List<PerformanceSubject>,
    val order_id: String,
    val totalQuestions: Int,
    val totalMarks: Int,
    val totalAttemptQuestions: Int,
    val totalUnattemptedQuestions: Int,
    val totalIncorrectQuestions: Int,
    val totalCorrectQuestions: Int,
    val score: String,
    val questions: List<Question>,
    val durationInMinutes: Int
)

data class PerformanceSubject(
    val subjectId: String,
    val subjectName: String,
    val totalQuestions: Int,
    val attemptedQuestions: Int,
    val unattemptedQuestions: Int,
    val correctQuestions: Int,
    val incorrectQuestions: Int,
    val score: String
)

data class Question(
    val questionId: String,
    val subjectId: String,
    val question: String,
    val options: List<String>,
    val selectedOption: Int?,  // Nullable: if not attempted, this will be null
    val questionType: String,
    val typeOfQuestion: String,
    val correctOption: Int,
    val solution: String,
    val subQuestions: List<ShowResultSubQuestion> // Updated type: list of SubQuestion
)

data class ShowResultSubQuestion(
    val subQuestionId: String,
    val questionId: String,  // Parent question ID
    val question: String,
    val options: List<String>,
    val selectedOption: Int?,  // Nullable
    val correctOption: Int,
    val solution: String
)
