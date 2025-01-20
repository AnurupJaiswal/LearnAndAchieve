package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
/*import com.google.android.gms.maps.model.LatLng*/

object SavedData {
    private const val FirebaseToken = "FirebaseToken"
    private const val Language = "Language"
    private const val ReferralCode = "ReferralCode"
    private const val popUp = "popUp"
    private const val latitude = "latitude"
    private const val CouponCode = "CouponCode"
    private const val longitude = "longitude"
    private const val CountryKey = "CountryKey"
    private const val CountryId = "CountryId"
    private const val CountryUuId = "CountryUuId"
    private const val Shared = "Shared"
    private const val GetStartClick = "GetStartClick"
    private const val isNotification = "isNotification"
    private const val mockTestList = "mockTestList"
    private const val isLogin = "isLogin"
    private var prefs: SharedPreferences? = null
    @JvmStatic
    val instance: SharedPreferences?
        get() {
            if (prefs == null) {
                prefs = PreferenceManager.getDefaultSharedPreferences(AppController.instance)
            }
            return prefs
        }

    fun getLanguage(): String? {
        return instance!!.getString(Language, Constants.english)
    }

    fun saveLanguage(role: String?) {
        val editor = instance!!.edit()
        editor.putString(Language, role)
        editor.apply()
    }

    fun getNotification(): Int {
        return instance!!.getInt(isNotification, 1)
    }

    fun saveNotification(status: Int) {
        val editor = instance!!.edit()
        editor.putInt(isNotification, status)
        editor.apply()
    }

    val mockTestHistory: String?
        get() = instance!!.getString(mockTestList, Constants.On)

    fun saveMockTestHistory(mockTestList: String?) {
        val editor = instance!!.edit()
        editor.putString(mockTestList, mockTestList)
        editor.apply()
    }

    fun getPopUp(): Boolean {
        return instance!!.getBoolean(popUp, false)
    }

    fun savePopUp(role: Boolean) {
        val editor = instance!!.edit()
        editor.putBoolean(popUp, role)
        editor.apply()
    }

    val startClick: Boolean
        get() = instance!!.getBoolean(GetStartClick, false)

    fun saveGetStartClick(role: Boolean) {
        val editor = instance!!.edit()
        editor.putBoolean(GetStartClick, role)
        editor.apply()
    }

    fun getFirebaseToken(): String? {
        return instance!!.getString(FirebaseToken, Constants.blank)
    }

    @JvmStatic
    fun saveFirebaseToken(token: String?) {
        val editor = instance!!.edit()
        editor.putString(FirebaseToken, token)
        editor.apply()
    }

    fun getCountryKey(): String? {
        return instance!!.getString(CountryKey, Constants.Default_Country_Region)
    }

    fun saveCountryKey(key: String?) {
        val editor = instance!!.edit()
        editor.putString(CountryKey, key)
        editor.apply()
    }

    fun getReferralCode(): String? {
        return instance!!.getString(ReferralCode, Constants.blank)
    }

    fun saveReferralCode(token: String?) {
        val editor = instance!!.edit()
        editor.putString(ReferralCode, token)
        editor.apply()
    }

    fun getCountryUuId(): String? {
        return instance!!.getString(CountryUuId, Constants.DefaultCountryUuId)
    }
    fun getLoginStatus(): String? {
        return instance!!.getString(isLogin, Constants.Default_Country_Region)
    }


    fun saveCountryUuId(Id: String?) {
        val editor = instance!!.edit()
        editor.putString(CountryUuId, Id)
        editor.apply()
    }

    /* public static String getBankStatus() {
        return getInstance().getString(Shared, Constants.blank);
    }

    public static void SaveBankStatus(String Id) {
        SharedPreferences.Editor editor = getInstance().edit();
        editor.putString(Shared, Id);
        editor.apply();

    }*/
    /*val currentLocation: LatLng
        get() {
            val lat = instance!!.getString(latitude, "0.0")!!.toDouble()
            val longi = instance!!.getString(longitude, "0.0")!!.toDouble()
            return LatLng(lat, longi)
        }*/

    fun saveCurrentLocation(location: Location) {
        val editor = instance!!.edit()
        editor.putString(latitude, location.latitude.toString())
        editor.putString(longitude, location.longitude.toString())
        editor.apply()
    }

    val couponcode: String?
        get() = instance!!.getString(CouponCode, Constants.blank)

    fun saveCouponCode(couponCode: String?) {
        val editor = instance!!.edit()
        editor.putString(CouponCode, couponCode)
        editor.apply()
    }

    val refferalCode: String?
        get() = instance!!.getString(ReferralCode, Constants.blank)

    fun saveRefferalCode(couponCode: String?) {
        val editor = instance!!.edit()
        editor.putString(ReferralCode, couponCode)
        editor.apply()
    }
}