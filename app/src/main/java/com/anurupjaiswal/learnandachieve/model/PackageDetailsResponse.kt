package com.anurupjaiswal.learnandachieve.model

class PackageDetailsResponse {
    var message: String? = null
    var packageDetails: PackageDetails? = null
}

class PackageDetails {
    var package_id: String? = null
    var packageName: String? = null
    var actualPrice: String? = null
    var discountedPrice: String? = null
    var mainImage: String? = null
    var discountCoordinator: String? = null
    var paidSyllabus: Boolean = false
    var isBharatSAT: Boolean = false
    var details: String? = null
}
