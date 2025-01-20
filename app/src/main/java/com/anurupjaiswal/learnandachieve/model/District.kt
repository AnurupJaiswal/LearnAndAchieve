package com.anurupjaiswal.learnandachieve.model

// Represents a District object
class Districts {
    var _id: String? = null
    var name: String? = null
    var state: State? = null
    var is_active: Boolean = false
    var is_deleted: Boolean = false
    var created_date: String? = null
    var updated_date: String? = null
}

// Represents the API response for districts
class DistrictApiResponse {
    var availableDataCount: Int = 0
    var data: List<Districts>? = null
}
