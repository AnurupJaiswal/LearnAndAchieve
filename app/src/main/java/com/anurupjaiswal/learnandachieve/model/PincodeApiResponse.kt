package com.anurupjaiswal.learnandachieve.model
class PincodeApiResponse {
    var availableDataCount: Int = 0
    var data: List<Pincode>? = null
}

class Pincode {
    var _id: String? = null
    var name: String? = null
    var taluka: Taluka? = null
    var district: District? = null
    var state: State? = null
    var isActive: Boolean = false
    var isDeleted: Boolean = false
    var createdDate: String? = null
    var updatedDate: String? = null
    var version: Int = 0
}

class Taluka {
    var id: String? = null
    var name: String? = null
    var district: String? = null
    var isActive: Boolean = false
    var isDeleted: Boolean = false
    var createdDate: String? = null
    var updatedDate: String? = null
    var version: Int = 0
}
