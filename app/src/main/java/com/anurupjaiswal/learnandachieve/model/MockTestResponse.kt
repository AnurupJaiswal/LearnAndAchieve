package com.anurupjaiswal.learnandachieve.model

data class MockTestResponse(
    val message: String,
    val data: List<MockTestItem>,
    val availableDataCount: Int
)

data class MockTestItem(
    val order_id: String,
    val package_id: String,
    val packageName: String,
    val validityInDays: Int,
    val mockTest_id: String,
    val mockTestName: String,
    val durationInMinutes: Int,
    val numberOfAttempts: Int,
    val remainingAttempts: Int,
    val isPackageExpired: Boolean,
    val subjectData: List<MockSubject>,
    val numberOfQuestions: Int,
    val totalQuestions: Int,
    val date: String,
    val expireDate: String
)


data class MockSubject(
    val subjectId: String,
    val subjectName: String
)

