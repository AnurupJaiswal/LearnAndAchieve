package com.anurupjaiswal.learnandachieve.model

data class VerifyPaymentResponse(
    val status: String,  // The status of the payment verification, like "success" or "failure"
    val message: String, // A message describing the status
    val data: PaymentData?  // Optionally, any additional data sent by the server
)

data class PaymentData(
    val paymentId: String,   // Payment ID
    val orderId: String,     // Order ID
    val amount: Double,      // Amount paid
    val currency: String     // Currency used in the transaction
)