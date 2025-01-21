package com.anurupjaiswal.learnandachieve.basic.database

import android.database.sqlite.SQLiteDatabase
import android.widget.ImageView
import com.anurupjaiswal.learnandachieve.R
import com.google.gson.annotations.SerializedName
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E



class User {
    @SerializedName("_id")
    var _id: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("middleName")
    var middleName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("dateOfBirth")
    var dateOfBirth: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("schoolName")
    var schoolName: String? = null

    @SerializedName("medium")
    var medium: String? = null

    @SerializedName("classId")
    var classId: String? = null

    @SerializedName("profilePicture")
    var profilePicture: String? = null

    @SerializedName("registerBy")
    var registerBy: String? = null

    @SerializedName("referralCode")
    var referralCode: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("mobile")
    var mobile: Long? = null

    @SerializedName("addressLineOne")
    var addressLineOne: String? = null

    @SerializedName("addressLineTwo")
    var addressLineTwo: String? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("district")
    var district: String? = null

    @SerializedName("taluka")
    var taluka: String? = null

    @SerializedName("pinCode")
    var pinCode: String? = null

    @SerializedName("is_active")
    var isActive: String? = null

    @SerializedName("is_registered")
    var isRegistered: String? = null

    @SerializedName("is_deleted")
    var isDeleted: String? = null

    @SerializedName("otp")
    var otp: String? = null

    @SerializedName("created_date")
    var createdDate: String? = null

    @SerializedName("updated_date")
    var updatedDate: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("class_name")
    var className: String? = null
}

class UserData {


    companion object {
        const val TABLE_NAME = "user_table"
        const val KEY_ID = "id" // Auto-incremented primary key for the table
        const val KEY_UserId = "_id" // ID from the response
        const val Key_FirstName = "firstName"
        const val Key_MiddleName = "middleName"
        const val Key_LastName = "lastName"
        const val Key_DateOfBirth = "dateOfBirth"
        const val Key_Gender = "gender"
        const val Key_SchoolName = "schoolName"
        const val Key_Medium = "medium"
        const val Key_ClassId = "classId"
        const val KEY_profilePic = "profilePicture"
        const val Key_RegisterBy = "registerBy"
        const val Key_ReferralCode = "referralCode"
        const val Key_Email = "email"
        const val KEY_Mobile = "mobile"
        const val Key_AddressLineOne = "addressLineOne"
        const val Key_AddressLineTwo = "addressLineTwo"
        const val Key_State = "state"
        const val Key_District = "district"
        const val Key_Taluka = "taluka"
        const val Key_PinCode = "pinCode"
        const val KEY_IsActive = "is_active"
        const val KEY_IsRegistered = "is_registered"
        const val KEY_IsDeleted = "is_deleted"
        const val KEY_Otp = "otp"
        const val KEY_CreatedDate = "created_date"
        const val KEY_UpdatedDate = "updated_date"
        const val Key_Password = "password"
        const val Key_ClassName = "class_name"
        const val Key_Token = "token"

        @JvmStatic
        fun CreateTable(db: SQLiteDatabase) {
            val CreateTableQuery = ("CREATE TABLE " + TABLE_NAME + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Auto-incremented
                    KEY_UserId + " TEXT," + // Stores `_id` from the response
                    Key_FirstName + " TEXT," +
                    Key_MiddleName + " TEXT," +
                    Key_LastName + " TEXT," +
                    Key_DateOfBirth + " TEXT," +
                    Key_Gender + " TEXT," +
                    Key_SchoolName + " TEXT," +
                    Key_Medium + " TEXT," +
                    Key_ClassId + " TEXT," +
                    KEY_profilePic + " TEXT," +
                    Key_RegisterBy + " TEXT," +
                    Key_ReferralCode + " TEXT," +
                    Key_Email + " TEXT," +
                    KEY_Mobile + " TEXT," +
                    Key_AddressLineOne + " TEXT," +
                    Key_AddressLineTwo + " TEXT," +
                    Key_State + " TEXT," +
                    Key_District + " TEXT," +
                    Key_Taluka + " TEXT," +
                    Key_PinCode + " TEXT," +
                    KEY_IsActive + " TEXT," +
                    KEY_IsRegistered + " TEXT," +
                    KEY_IsDeleted + " TEXT," +
                    KEY_Otp + " TEXT," +
                    KEY_CreatedDate + " TEXT," +
                    KEY_UpdatedDate + " TEXT," +
                    Key_Password + " TEXT," +
                    Key_Token + " TEXT," +
                    Key_ClassName + " TEXT" +
                    " )")
            E("CreateTableQuery::$CreateTableQuery")
            db.execSQL(CreateTableQuery)
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        }
    }

    fun loadImage(imageView: ImageView, imageUrl: String) {
        Utils.Picasso(Const.HOST_URL + imageUrl, imageView, R.drawable.dummy)
    }
}

