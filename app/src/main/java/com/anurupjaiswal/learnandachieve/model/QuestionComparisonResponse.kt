package com.anurupjaiswal.learnandachieve.model

import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionStatus

data class QuestionComparisonResponse(
    val message: String,
    val data: QuestionComparisonData
)

data class QuestionComparisonData(
    val mockTestId: String,
    val mockTestName: String,
    val durationInMinutes: Int,
    val bharatSatExamId: String,
    val hallTicketNumber: String,
    val bharatSatExamName: String,
    val examStartTime: String,
    val examEndTime: String,
    val bharatSatExamDate: String,
    val subjects: List<QuestionComparisonSubject>,
    val subjectQuestions: List<SubjectQuestions>
)

data class QuestionComparisonSubject(
    val subjectId: String,
    val subjectName: String,
    val numberOfQuestions: Int,
    val medium: String
)

data class SubjectQuestions(
    val subjectId: String,
    val questions: List<QuestionItem>
)

data class QuestionItem(
    val questionId: String,
    val subjectId: String,
    val question: String,
    val options: List<String>,
    val questionType: String,
    val typeOfQuestion: String,
    val subQuestions: List<SubQuestion>,
    var status: QuestionStatus = QuestionStatus.NOT_VIEWED

)

data class SubQuestion(
    val subQuestionId: String,
    val questionId: String,
    val question: String,
    val options: List<String>
)
