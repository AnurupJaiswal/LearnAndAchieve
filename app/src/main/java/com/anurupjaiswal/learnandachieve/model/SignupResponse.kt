package com.anurupjaiswal.learnandachieve.model



data class SignupResponse(
    val message: String,  // The message, like "OTP Sent Successfully"
    val token: String?    // The token returned in the response
)
