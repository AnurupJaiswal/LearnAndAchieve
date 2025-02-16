package com.anurupjaiswal.learnandachieve.basic.retrofit

import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants


interface Const {
    companion object {
        //Environment  **NOTE**  Change "Live" With "Debug"  When Going Live
        const val Development = Constants.Debug

        //Live
        const val HOST_URL = "https://api.learnandachieve.in/"
        //Debug
       // const val HOST_URL = "https://stage-api.learnandachieve.in/"

        const val baseUrlForImage = "https://stage-api.learnandachieve.in/uploads/";


        const val razorpayKeyName: String = "rzp_live_S6CW342Go2QiOT"

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
        const val FAQ_CATEGORY_ID = "faq_Category_id"
        const val TALUKA_ID = "taluka_id"
        const val VERIFY_OTP = "user/verifyOtp"
        const val RESET_PASSWORD = "user/resetPassword"
        const val PACKAGE_GET_ALL = "package/getAll"
        const val ADD_TO_CART = "package/addToCart"
        const val GET_PACKAGE_DETAILS_BY_ID = "package/getDetailById"
        const val GET_ALL_CARDS = "package/getAllCart"
        const val DELETE_CART_API = "package/deleteCart/{cart_id}"
        const val STUDY_MATERIALS_GETALL = "studyMaterials/getAll"
        const val GET_ALL_MODULE_BY_SUBJECT = "studyMaterials/getAllModuleBySubject"
        const val GET_ALL_TOPIC = "studyMaterials/getAllTopics"
        const val GET_USER_DETAILS= "user/details"
        const val GET_ODER_ALL= "order/getAll"
        const val CHANGE_PASSWORD= "user/changePassword"
        const val UPDATE_PROFILE= "user/updateProfile"
        const val DELETE_PROFILE_PICTURE= "user/deleteprofilePicture"
        const val DELETE_USER = "user/deleteUser"
        const val ADD_COORDINATOR = "coordinator/add-coordinator"
        const val GET_TERMS_CONDITIONS = "termsConditions/get-terms-condition"
        const val PRIVACY_POLICY = "privacyPolicy/get-privacy-policy"
        const val CANCELLATION_POLICY = "cancellationPolicy/get-cancellation-Condition"
        const val FAQ_ALL= "faq/getAll"
        const val   CREATE_ORDER = "package/createOrder"
        const val VERIFY_PAYMENT = "package/verifyPayment"
        const val MOCK_TEST_ALL = "mockTest/getAll"
        const val ALL_BLOG_APP = "blog/getAllBlogApp"
        const val BLOG_DETAILS_BY_ID = "blog/getBlogDetailsById"
        const val BLOG_GET_ALL = "blog/getAll"
        const val GET_MOCK_TEST_QUESTIONS = "mockTest/getAllQuestionById"
        const val SUBMIT_MOCK_TEST = "mockTest/submitMockTest"
        const val VIEW_RESULT = "mockTest/viewResult"
        const val SHOW_RESULT = "mockTest/getShowResults"


    }
}
