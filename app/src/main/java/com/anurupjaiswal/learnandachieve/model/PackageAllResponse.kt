package com.anurupjaiswal.learnandachieve.model

data class PackageResponse(
    val message: String,
    val packages: List<PackageData>
)

data class PackageData(
    val package_id: String,
    val packageName: String,
    val actualPrice: String,
    val discountedPrice: String,
    val mainImage: String,
    val discountCoordinator: String,
    val paidSyllabus: Boolean,
    val isBharatSAT: Boolean
)
