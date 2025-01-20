package com.anurupjaiswal.learnandachieve.model

data class PersonalDetails(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String,
    val schoolName: String,
    val classId: String,
    val registeredBy: String
)
