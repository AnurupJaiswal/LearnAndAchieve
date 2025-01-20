package com.anurupjaiswal.learnandachieve.model

data class PopularPackageItem(
    val title: String,
    val description: String,
    val discountedPrice: String,
    val originalPrice: String,
    val imageUrl: String,val
    showPopular: Boolean = false
)
