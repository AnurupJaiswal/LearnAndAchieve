package com.anurupjaiswal.learnandachieve.model
data class TopicResponse(
    val message: String,
    val topicList: List<Topic>
)

data class Topic(
    val topic_id: String,
    val topic_name: String,
    val youtube_links: List<String>,
    val subject_id: String,
    val medium: String,
    val uploaded_files: List<Any>,
    val created_date: String
)
