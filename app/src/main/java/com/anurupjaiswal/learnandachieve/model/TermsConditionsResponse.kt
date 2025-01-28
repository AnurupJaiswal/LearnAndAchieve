package com.anurupjaiswal.learnandachieve.model

data class TermsConditionsResponse(
    val message: String,
    val data: TermsConditionData
)

data class TermsConditionData(
    val _id: String,
    val title: String,
    val details: String,
    val is_active: Boolean,
    val is_deleted: Boolean
)