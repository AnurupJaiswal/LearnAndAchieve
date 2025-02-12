package com.anurupjaiswal.learnandachieve.model

// ShowResultReponce.kt
data class ShowResultReponce(
    val message: String,
    val data: List<ShowResultData>,
    val availableDataCount: Int
)

data class ShowResultData(
    val mockTestSubmissions_id: String,
    val mockTest_id: String,
    val package_id: String,
    val score: String,
    val totalNumberOfMarks: Int,
    val mockTestName: String,
    val durationInMinutes: Int,
    val packageName: String
)
