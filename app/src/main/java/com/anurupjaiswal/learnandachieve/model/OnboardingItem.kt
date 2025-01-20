package com.anurupjaiswal.learnandachieve.model

data class OnboardingItem(
    val imageResId: Int,
    val title: String,
    val description: String,
    val backgroundColor: Int,// Color for each item
    val activeDotColor: Int,   // Active dot color for each page
    val inactiveDotColor: Int // New property for inactive dot color

)
