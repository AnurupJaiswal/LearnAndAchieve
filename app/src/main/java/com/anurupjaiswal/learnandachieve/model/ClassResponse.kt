package com.anurupjaiswal.learnandachieve.model

class ClassData {
    var _id: String = ""
    var class_name: String = ""
    var class_end_date: String = ""
    var is_active: Boolean = false
    var is_deleted: Boolean = false
    var created_date: String = ""
    var updated_date: String = ""
    var __v: Int = 0
}

class ClassResponse {
    var message: String = ""
    var data: List<ClassData> = emptyList()
}
