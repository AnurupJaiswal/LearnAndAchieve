package com.anurupjaiswal.learnandachieve.model

data class OrderHistoryItem(
    val dateTime: String,
    val title: String,
    val orderId: String,
    val transactionId: String,
    val amount: String
)