package com.anurupjaiswal.learnandachieve.model

data class BlogResponse(
    val message: String,
    val data: DeailsBlogData
)

data class DeailsBlogData(
    val blogCategoryData: DeailsBlogCategoryData,
    val relatedBlogs: List<RelatedBlog>
)

data class DeailsBlogCategoryData(
    val blog_id: String,
    val title: String,
    val details: String,
    val author_id: String,
    val name: String,
    val date: String,
    val briefIntro: String,
    val mainImage: String,
    val featuredImage: String
)

data class RelatedBlog(
    val blog_id: String,
    val name: String,
    val author_id: String,
    val blog_category_id: String,
    val title: String,
    val date: String,
    val briefIntro: String,
    val details: String,
    val mainImage: String,
    val featuredImage: String
)
