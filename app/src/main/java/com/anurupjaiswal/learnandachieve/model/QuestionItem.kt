package com.anurupjaiswal.learnandachieve.model

data class QuestionItem(
    val questionNumber: String,
    val questionTitle: String,
    val paragraphText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    var selectedOptionIndex: Int? = null // Track the selected option index for each item

)
