package com.anurupjaiswal.learnandachieve.model

data class Coordinator(
    val name: String,
    val email: String,
    val mobile: String,
    val referralCode: String
)

data class CheckReferralResponse(
    val message: String,
    val coordinator: Coordinator?
)
