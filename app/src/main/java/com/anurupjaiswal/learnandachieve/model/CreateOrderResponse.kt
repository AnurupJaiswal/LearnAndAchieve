package com.anurupjaiswal.learnandachieve.model

data class CreateOrderRequest(
    val amount: Int,
    val currency: String,
    val notes: Notes
)

data class Notes(
    val referralCode: String,
    val type: String,
    val userId: String
)

data class CreateOrderResponse(
    val id: String,
    val entity: String,
    val amount: Int,
    val amount_due: Int,
    val amount_paid: Int,
    val attempts: Int,
    val created_at: Long,
    val currency: String,
    val offer_id: String?,
    val receipt: String,
    val notes: Notes
)