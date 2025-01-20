package com.anurupjaiswal.learnandachieve.model

data class PackageModel(
    val title: String,
    val price: String,
    val originalPrice: String,
    val imageRes: Int,
    val description: String? = null
)
