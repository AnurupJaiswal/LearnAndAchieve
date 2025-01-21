package com.anurupjaiswal.learnandachieve.basic.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E

class UserDataHelper(cx: Context) {
    private val dm: DataManager
    var cx: Context
    private var db: SQLiteDatabase? = null

    init {
        instance = this
        this.cx = cx
        dm = DataManager(
            cx,
            DataManager.DATABASE_NAME,
            null,
            DataManager.DATABASE_VERSION
        )
    }

    /**
     * Open the database for writing.
     */
    fun open() {
        db = dm.writableDatabase
    }

    /**
     * Close the database.
     */
    fun close() {
     //   db?.close()
    }

    /**
     * Open the database for reading.
     */
    fun read() {
        db = dm.readableDatabase
    }

    /**
     * Delete a user by their ID from the table.
     *
     * @param userData The user to delete.
     */
    fun delete(userData: User) {
        open()
        db!!.delete(
            UserData.TABLE_NAME,
            "${UserData.KEY_UserId} = ?",
            arrayOf(userData._id)
        )
      //  close()
    }

    /**
     * Delete all data from the table.
     */
    fun deleteAll() {
        open()
        db!!.delete(UserData.TABLE_NAME, null, null)
     //   close()
    }

    /**
     * Check if a user exists in the table.
     *
     * @param userData The user to check.
     * @return True if the user exists, false otherwise.
     */
    private fun isExist(userData: User): Boolean {
        read()
        val cursor = db!!.rawQuery(
            "SELECT * FROM ${UserData.TABLE_NAME} WHERE ${UserData.KEY_UserId} = ?",
            arrayOf(userData._id)
        )
        val exists = cursor.moveToFirst()
    //    cursor.close()
        return exists
    }

    /**
     * Insert or update user data in the table.
     *
     * @param userData The user data to insert or update.
     */
    fun insertData(userData: User?) {
        if (userData == null) return
        open()
        val values = ContentValues().apply {
            put(UserData.KEY_UserId, userData._id)
            put(UserData.Key_Token,userData.token)
            put(UserData.Key_FirstName, userData.firstName)
            put(UserData.Key_MiddleName, userData.middleName)
            put(UserData.Key_LastName, userData.lastName)
            put(UserData.Key_DateOfBirth, userData.dateOfBirth)
            put(UserData.Key_Gender, userData.gender)
            put(UserData.Key_SchoolName, userData.schoolName)
            put(UserData.Key_Medium, userData.medium)
            put(UserData.Key_ClassId, userData.classId)
            put(UserData.KEY_profilePic, userData.profilePicture)
            put(UserData.Key_RegisterBy, userData.registerBy)
            put(UserData.Key_ReferralCode, userData.referralCode)
            put(UserData.Key_Email, userData.email)
            put(UserData.KEY_Mobile, userData.mobile)
            put(UserData.Key_AddressLineOne, userData.addressLineOne)
            put(UserData.Key_AddressLineTwo, userData.addressLineTwo)
            put(UserData.Key_State, userData.state)
            put(UserData.Key_District, userData.district)
            put(UserData.Key_Taluka, userData.taluka)
            put(UserData.Key_PinCode, userData.pinCode)
            put(UserData.KEY_IsActive, userData.isActive)
            put(UserData.KEY_IsRegistered, userData.isRegistered)
            put(UserData.KEY_IsDeleted, userData.isDeleted)
            put(UserData.KEY_Otp, userData.otp)
            put(UserData.KEY_CreatedDate, userData.createdDate)
            put(UserData.KEY_UpdatedDate, userData.updatedDate)
            put(UserData.Key_Password, userData.password)
            put(UserData.Key_ClassName, userData.className)

        }

        if (!isExist(userData)) {
            E("Insert successful: $values")
            db!!.insert(UserData.TABLE_NAME, null, values)
        } else {
            E("Update successful: $values")
            db!!.update(
                UserData.TABLE_NAME,
                values,
                "${UserData.KEY_UserId} = ?",
                arrayOf(userData._id)
            )
        }
       // close()
    }

    /**
     * Retrieve a list of users from the table.
     *
     * @return A list of users.
     */
    @get:SuppressLint("Range")
    val list: ArrayList<User>
        get() {
            val userList = ArrayList<User>()
            read()
            val cursor = db!!.rawQuery("SELECT * FROM ${UserData.TABLE_NAME}", null)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val userData = User().apply {
                        _id = cursor.getString(cursor.getColumnIndex(UserData.KEY_UserId))
                        firstName = cursor.getString(cursor.getColumnIndex(UserData.Key_FirstName))
                        middleName = cursor.getString(cursor.getColumnIndex(UserData.Key_MiddleName))
                        lastName = cursor.getString(cursor.getColumnIndex(UserData.Key_LastName))
                        dateOfBirth = cursor.getString(cursor.getColumnIndex(UserData.Key_DateOfBirth))
                        gender = cursor.getString(cursor.getColumnIndex(UserData.Key_Gender))
                        schoolName = cursor.getString(cursor.getColumnIndex(UserData.Key_SchoolName))
                        medium = cursor.getString(cursor.getColumnIndex(UserData.Key_Medium))
                        classId = cursor.getString(cursor.getColumnIndex(UserData.Key_ClassId))
                        profilePicture = cursor.getString(cursor.getColumnIndex(UserData.KEY_profilePic))
                        registerBy = cursor.getString(cursor.getColumnIndex(UserData.Key_RegisterBy))
                        referralCode = cursor.getString(cursor.getColumnIndex(UserData.Key_ReferralCode))
                        email = cursor.getString(cursor.getColumnIndex(UserData.Key_Email))
                        mobile = cursor.getLong(cursor.getColumnIndex(UserData.KEY_Mobile))
                        addressLineOne = cursor.getString(cursor.getColumnIndex(UserData.Key_AddressLineOne))
                        addressLineTwo = cursor.getString(cursor.getColumnIndex(UserData.Key_AddressLineTwo))
                        state = cursor.getString(cursor.getColumnIndex(UserData.Key_State))
                        district = cursor.getString(cursor.getColumnIndex(UserData.Key_District))
                        taluka = cursor.getString(cursor.getColumnIndex(UserData.Key_Taluka))
                        pinCode = cursor.getString(cursor.getColumnIndex(UserData.Key_PinCode))
                        isActive = cursor.getString(cursor.getColumnIndex(UserData.KEY_IsActive))
                        isRegistered = cursor.getString(cursor.getColumnIndex(UserData.KEY_IsRegistered))
                        isDeleted = cursor.getString(cursor.getColumnIndex(UserData.KEY_IsDeleted))
                        otp = cursor.getString(cursor.getColumnIndex(UserData.KEY_Otp))
                        createdDate = cursor.getString(cursor.getColumnIndex(UserData.KEY_CreatedDate))
                        updatedDate = cursor.getString(cursor.getColumnIndex(UserData.KEY_UpdatedDate))
                        password = cursor.getString(cursor.getColumnIndex(UserData.Key_Password))
                        className = cursor.getString(cursor.getColumnIndex(UserData.Key_ClassName))
                        token = cursor.getString(cursor.getColumnIndex(UserData.Key_Token))
                    }
                    userList.add(userData)
                } while (cursor.moveToNext())
             //   cursor.close()
            }
          //  close()
            return userList
        }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: UserDataHelper
            private set
    }
}
