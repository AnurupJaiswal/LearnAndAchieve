package com.anurupjaiswal.learnandachieve.basic.database

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class UserModelData {


    @SerializedName("_id")
    @Expose
    var id: String? = null

    @SerializedName("user_name")
    @Expose
    var userName: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("profile_img")
    @Expose
    var profileImg: String? = null



    @SerializedName("date_of_birth")
    @Expose
    var dateOfBirth: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null


    var fcmId: String? = null


    companion object {
        const val TABLE_NAME = "journal"

        //    All Key
        const val KEY_ID = "_id"
        const val KEY_UserId = "userId"
        const val Key_UserName = "userName"
        const val Key_Email = "email"
        const val Key_Gender = "gender"
        const val KEY_ProfileImg = "profileImg"
        const val Key_DateOfBirth = "dateOfBirth"
        const val Key_FcmId = "fcmId"
        const val KEY_Token = "token"
        const val KEY_Profile = "profilePic"

        @JvmStatic
        fun CreateTable(db: SQLiteDatabase) {
            Log.e("","crete========================= tabel UserModolData")
            val CreateTableQuery =
                ("CREATE TABLE $TABLE_NAME (" +
                        "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$KEY_UserId TEXT, " +
                        "$Key_UserName TEXT, " +
                        "$Key_Email TEXT, " +
                        "$Key_Gender TEXT, " +
                        "$KEY_ProfileImg TEXT, " +
                        "$KEY_Profile TEXT, " +
                        "$Key_FcmId TEXT, " +
                        "$KEY_Token TEXT)")
            /*Utils.E("CreateTableQuery::$CreateTableQuery")*/
            db.execSQL(CreateTableQuery)
        }



        /**
         * @param db SQLiteDatabase
         */
        @JvmStatic
        fun dropTable(db: SQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        }
    }


}