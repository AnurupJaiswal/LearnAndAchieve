package com.anurupjaiswal.learnandachieve.model

import com.anurupjaiswal.learnandachieve.basic.database.User


data class GetUserResponse(
    val message: String,
    val user: GetUser
)
data class GetUser(
    val user_id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String,
    val schoolName: String,
    val profilePicture: String?,
    val registerBy: String,
    val referralCode: String,
    val email: String,
    val mobile: Long,
    val addressLineOne: String,
    val addressLineTwo: String,
    val state: String,
    val district: String,
    val taluka: String,
    val pinCode: String,
    val is_active: Boolean,
    val is_registered: Boolean,
    val is_deleted: Boolean,
    val created_date: String,
    val updated_date: String,
    val Active_devices: List<ActiveDevice>,
    val cartCount: Int,
    val medium: String,
    val class_id: String,
    val class_name: String,
    val hallTicketNumber: String,
    val eHallTicketId: String,
    val bharatSatExamDate: String,
    val examStartTime: String,
    val examEndTime: String,
    val bharatSatExamId: String,
    val examAttempted: Boolean,
    val examTimeOver: Boolean,
    val paidSyllabus: Boolean,
    val smartSchoolCredentials: SmartSchoolCredentials,
    val shouldShowBharatSATTab: Boolean

)


data class ActiveDevice(
    val deviceType: String,
    val deviceName: String,
    val browser: String,
    val currentToken: String,
    val lastLogin: String,
    val _id: String
)


data class SmartSchoolCredentials(
    val username: String,
    val password: String
)
class LoginData {
    var token: String? = null
    var user: User? = null
    var data: List<ActiveDevice> = emptyList()
    var message: String? = null
}

