package com.anurupjaiswal.learnandachieve.model

data class GetAllBlogAppResponse(
    val message: String,
    val data: Data
)

data class Data(
    val blogCategoryData: List<BlogCategoryData>,
    val BlogData: List<BlogData>

)

data class BlogCategoryData(
    val blog_Category_id: String,
    val categoryName: String
)

data class BlogData(
    val blog_id: String,
    val author_id: String,
    val name: String,
    val blog_category_id: String,
    val title: String,
    val date: String,
    val briefIntro: String,
    val details: String,
    val mainImage: String,
    val featuredImage: String
)
