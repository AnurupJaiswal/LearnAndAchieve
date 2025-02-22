package com.anurupjaiswal.learnandachieve.model

import com.google.gson.annotations.SerializedName



data class SubmitBharatSetExamRequest(
    val bharatSatExamId: String,
    val eHallTicketId: String,
    val startTime: String,          // e.g., "6:57:29 pm"
    val endTime: String,            // e.g., "6:58:37 pm"
    val submitted_date: String,     // e.g., "21/2/2025, 6:58:37 pm"
    val submittedTime: String,      // elapsed time formatted as "HH:mm:ss"
    val submittedAnswers: List<SubmittedAnswer>,
    val numberOfQuestionsData: List<BharatSatExamNumberOfQuestionsData>
)

data class BharatSatExamNumberOfQuestionsData(
    val subjectId: String,
    val numberOfQuestionsBank: Int,
    val numberOfQuestionsBharatSat: Int
)


data class BharatSatSubmitMockTestResponse(
    @SerializedName("message") val message: String
)
