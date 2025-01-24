package com.anurupjaiswal.learnandachieve.basic.retrofit

import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants


interface Const {
    companion object {
        //Environment  **NOTE**  Change "Live" With "Debug"  When Going Live
        const val Development = Constants.Debug

        //Live

        //Debug
        const val HOST_URL = "https://stage-api.learnandachieve.in/"

        const val baseUrlForImage = "https://stage-api.learnandachieve.in/uploads/";


        const val USER_LOGIN = "user/login"
        const val GET_ALL_CLASSES = "class/getAllClasses"
        const val GET_STATES = "state/all"
        const val GET_DISTRICTS = "district"
        const val GET_TALUKAS = "taluka"
        const val GET_PINCODE = "pincode"
        const val REGISTER_USER = "user/register"
        const val VERIFY_OTP_REGISTRATION = "user/verify-otp-registration"
        const val FORGET_PASSWORD = "user/forgetPassword"

        // API Query Parameters
        const val OFFSET = "offset"
        const val LIMIT = "limit"
        const val SEARCH_QUERY = "searchQuery"
        const val STATE_ID = "state_id"
        const val DISTRICT_ID = "district_id"
        const val TALUKA_ID = "taluka_id"
const val VERIFY_OTP = "user/verifyOtp"
        const val RESET_PASSWORD = "user/resetPassword"
const val PACKAGE_GET_ALL = "package/getAll"

const val ADD_TO_CART = "package/addToCart"

const val GET_PACKAGE_DETAILS_BY_ID ="package/getDetailById"

        const val GET_ALL_CARDS = "package/getAllCart"
        const val DELETE_CART_API = "package/deleteCart/{cart_id}"
        const val STUDY_MATERIALS_GETALL = "studyMaterials/getAll"
        const val GET_ALL_MODULE_BY_SUBJECT = "studyMaterials/getAllModuleBySubject"


    }
}
