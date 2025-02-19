package com.anurupjaiswal.learnandachieve.model


// Represents the response for Talukas
class TalukaApiResponse {
    var availableDataCount: Int = 0
    var data: List<Talukas>? = null
}

// Represents a Taluka
class Talukas {
    var _id: String? = null
    var name: String? = null
    var district: District? = null
    var state: State? = null
    var is_active: Boolean = false
    var is_deleted: Boolean = false
    var created_date: String? = null
    var updated_date: String? = null
    var __v: Int = 0
}

// Represents a District
class District {
    var _id: String? = null
    var name: String? = null
    var state: String? = null
    var is_active: Boolean = false
    var is_deleted: Boolean = false
    var created_date: String? = null
    var updated_date: String? = null
    var __v: Int = 0
}


