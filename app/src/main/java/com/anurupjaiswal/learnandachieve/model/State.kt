package com.anurupjaiswal.learnandachieve.model

class State {
    var _id: String? = null
    var name: String? = null
    var is_active: Boolean = false
    var is_deleted: Boolean = false
    var created_date: String? = null
    var updated_date: String? = null
    var __v: Int = 0
}

class StateApiResponse {
    var availableDataCount: Int = 0
    var data: List<State>? = null
}
