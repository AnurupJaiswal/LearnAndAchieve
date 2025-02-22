package com.anurupjaiswal.learnandachieve.model

import com.google.gson.annotations.SerializedName

data class SubmitMockTestRequest(
    @SerializedName("mockTest_id") val mockTest_id: String,
    @SerializedName("package_id") val package_id: String,
    @SerializedName("order_id") val order_id: String,
    @SerializedName("submittedAnswers") val submittedAnswers: List<SubmittedAnswer>,
    @SerializedName("numberOfQuestionsData") val numberOfQuestionsData: List<NumberOfQuestionsData>,
    @SerializedName("submittedTime") val submittedTime: String
)



data class SubmittedAnswer(
    @SerializedName("question_id") val question_id: String,
    @SerializedName("sub_question_id") val sub_question_id: String? = null,
    @SerializedName("selectedOption") val selectedOption: Int,
    @SerializedName("typeOfQuestion") val typeOfQuestion: String
)

data class NumberOfQuestionsData(
    @SerializedName("subjectId") val subjectId: String,
    @SerializedName("numberOfQuestions") val numberOfQuestions: Int
)

data class SubmitMockTestResponse(
    @SerializedName("message") val message: String
)
