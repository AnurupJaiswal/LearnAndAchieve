package com.anurupjaiswal.learnandachieve.model

data class FAQResponse(
    val message: String,
    val data: FAQData,
    val availableDataCount: Int
)

data class FAQData(
    val faqCategoryData: List<FAQCategory>,
    val faqsData: List<FAQQuestion>
)

data class FAQCategory(
    val faq_Category_id: String,
    val faqCategoryName: String,
    var questions: List<FAQQuestion> = emptyList()  // Will populate later based on category
)

data class FAQQuestion(
    val faqs_id: String,
    val faq_category_id: String,
    val question: String,
    val answer: String
)