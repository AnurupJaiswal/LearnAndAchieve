package com.anurupjaiswal.learnandachieve.basic.retrofit

import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.ApiResponse
import com.anurupjaiswal.learnandachieve.model.BlogResponse
import com.anurupjaiswal.learnandachieve.model.CartResponse
import com.anurupjaiswal.learnandachieve.model.ChangePasswordResponse
import com.anurupjaiswal.learnandachieve.model.ClassResponse
import com.anurupjaiswal.learnandachieve.model.CreateOrderRequest
import com.anurupjaiswal.learnandachieve.model.CreateOrderResponse
import com.anurupjaiswal.learnandachieve.model.DeleteCartResponse
import com.anurupjaiswal.learnandachieve.model.DistrictApiResponse
import com.anurupjaiswal.learnandachieve.model.FAQResponse
import com.anurupjaiswal.learnandachieve.model.ForgetPasswordResponse
import com.anurupjaiswal.learnandachieve.model.GetAllBlogAppResponse
import com.anurupjaiswal.learnandachieve.model.GetAllStudyMaterial
import com.anurupjaiswal.learnandachieve.model.LoginData
import com.anurupjaiswal.learnandachieve.model.Module
import com.anurupjaiswal.learnandachieve.model.ModuleResponse
import com.anurupjaiswal.learnandachieve.model.PackageDetailsResponse
import com.anurupjaiswal.learnandachieve.model.PackageResponse
import com.anurupjaiswal.learnandachieve.model.PincodeApiResponse
import com.anurupjaiswal.learnandachieve.model.RegistrationVerifyOtpResponse
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.anurupjaiswal.learnandachieve.model.StateApiResponse
import com.anurupjaiswal.learnandachieve.model.TalukaApiResponse
import com.anurupjaiswal.learnandachieve.model.TopicResponse
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.anurupjaiswal.learnandachieve.model.MockTestResponse
import com.anurupjaiswal.learnandachieve.model.OrderHistoryResponse
import com.anurupjaiswal.learnandachieve.model.QuestionComparisonResponse
import com.anurupjaiswal.learnandachieve.model.TermsConditionsResponse
import com.anurupjaiswal.learnandachieve.model.VerifyOtpResponse
import com.anurupjaiswal.learnandachieve.model.VerifyPaymentResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {


    @FormUrlEncoded
    @POST(Const.USER_LOGIN)
    fun userLogin(@FieldMap hm: HashMap<String, String?>): Call<LoginData>?

    @GET(Const.GET_ALL_CLASSES)
    fun getAllClasses(): Call<ClassResponse>


    @GET(Const.GET_STATES)
    fun getStates(): Call<StateApiResponse>

    @GET(Const.GET_DISTRICTS)
    fun getDistricts(
        @Query(Const.OFFSET) offset: Int,
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.SEARCH_QUERY) searchQuery: String,
        @Query(Const.STATE_ID) stateId: String
    ): Call<DistrictApiResponse>

    @GET(Const.GET_TALUKAS)
    fun getTalukas(
        @Query(Const.OFFSET) offset: Int,
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.SEARCH_QUERY) searchQuery: String,
        @Query(Const.STATE_ID) stateId: String,
        @Query(Const.DISTRICT_ID) districtId: String
    ): Call<TalukaApiResponse>

    @GET(Const.GET_PINCODE)
    fun getPincodeData(
        @Query(Const.OFFSET) offset: Int,
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.SEARCH_QUERY) searchQuery: String,
        @Query(Const.STATE_ID) stateId: String,
        @Query(Const.DISTRICT_ID) districtId: String,
        @Query(Const.TALUKA_ID) talukaId: String
    ): Call<PincodeApiResponse>


    @POST(Const.REGISTER_USER)
    fun registerUser(@Body requestBody: RequestBody): Call<SignupResponse>

    @POST(Const.VERIFY_OTP_REGISTRATION)
    fun verifyOtpRegestation(
        @Header(Constants.Authorization) authToken: String, @Body otpRequest: JsonObject
    ): Call<RegistrationVerifyOtpResponse>


    @POST(Const.FORGET_PASSWORD)
    fun forgotPassword(@Body body: RequestBody): Call<ForgetPasswordResponse>


    @POST(Const.VERIFY_OTP)
    fun verifyOtp(  @Header(Constants.Authorization) authToken: String,
                     @Body params: Map<String, String?>): Call<VerifyOtpResponse>


    @POST(Const.RESET_PASSWORD)
    fun resetPassword(
        @Header(Constants.Authorization)
        token: String,
        @Body requestBody: Map<String, String>
    ): Call<ForgetPasswordResponse>

    @GET(Const.PACKAGE_GET_ALL)
    fun getPackages(
        @Header(Constants.Authorization)
        token: String,
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.OFFSET) offset: Int
    ): Call<PackageResponse>


    @POST(Const.ADD_TO_CART)
    fun addToCart(
        @Header(Constants.Authorization) token: String?,   // Pass the token in the header
        @Body body: Map<String, String?>          // Use a Map for the request body
    ): Call<CartResponse>

    @GET(Const.GET_PACKAGE_DETAILS_BY_ID)
    fun getPackageDetails(
        @Header(Constants.Authorization) token: String?,   // Pass the token in the header
        @Query(Constants.PackageId) packageId: String?
    ): Call<PackageDetailsResponse>

    @GET(Const.GET_ALL_CARDS)
    fun getCartData(@Header(Constants.Authorization) token: String): Call<AllCartResponse>


    @DELETE(Const.DELETE_CART_API)
    fun deleteCartItem(
        @Header(Constants.Authorization) authToken: String, // Authentication token for the request
        @Path(Constants.cartId) cart_id: String // Cart item ID to be deleted
    ): Call<DeleteCartResponse>

    @GET(Const.STUDY_MATERIALS_GETALL)
    fun getStudyMaterials(
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.OFFSET) offset: Int,
        @Header(Constants.Authorization) authorization: String
    ): Call<GetAllStudyMaterial>

    @GET(Const.GET_ALL_MODULE_BY_SUBJECT)
    fun getAllModulesBySubject(
        @Header(Constants.Authorization) authorization: String,
        @Query(Constants.subjectId) subjectId: String,
        @Query(Constants.medium) medium: String
    ): Call<ModuleResponse>

    @GET("studyMaterials/getAllTopics")
    fun getAllTopics(
        @Header(Constants.Authorization) authorization: String,
        @Query(Constants.moduleId) moduleId: String
    ): Call<TopicResponse>

    @GET("user/details")
     fun getUserDetails(
        @Header(Constants.Authorization) authorization: String
    ): Call<GetUserResponse>

    @GET("order/getAll")
    fun getOrderHistory(
        @Header(Constants.Authorization) authorization: String
    ): Call<OrderHistoryResponse>



    @POST("user/changePassword")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body body: HashMap<String, String>
    ): Call<ChangePasswordResponse>
    @Multipart
    @POST("user/updateProfile")
    fun updateProfile(
        @Header("Authorization") token: String, // Token in Authorization header
        @Part profilePicture: MultipartBody.Part // Profile picture
    ): Call<GetUserResponse>

    @DELETE("user/deleteprofilePicture")
    fun deleteProfilePicture(
        @Header("Authorization") token: String
    ): Call<ApiResponse>

    @POST("user/deleteUser")
    fun deleteUser(@Header(Constants.Authorization) token: String): Call<ApiResponse>


    @POST("coordinator/add-coordinator")
    fun addCoordinator(
        @Header(Constants.Authorization) token: String,
        @Body params: HashMap<String, Any>
    ): Call<ApiResponse>

    @GET("termsConditions/get-terms-condition")
    fun getTermsConditions(): Call<TermsConditionsResponse>

    @GET("privacyPolicy/get-privacy-policy")
    fun getPrivacyPolicy(): Call<TermsConditionsResponse>

    @GET("cancellationPolicy/get-cancellation-Condition")
    fun getCancellationPolicy(): Call<TermsConditionsResponse>

    @GET("user/getAboutUsPageContent")
    fun getAboutUsPageContent(): Call<TermsConditionsResponse>

    @GET("faq/getAll")
    fun getCategories(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Call<FAQResponse>

    @GET("faq/getAll")
    fun getFAQsByCategory(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("faq_Category_id") categoryId: String
    ): Call<FAQResponse>


    @POST("package/createOrder")
    fun createOrder(
        @Header("Authorization") token: String?,
        @Body paymentData: HashMap<String, String>    ): Call<CreateOrderResponse>



    // Endpoint for verifying payment
    @POST("package/verifyPayment")
    fun verifyPayment(
        @Header("Authorization") authToken: String,  // Send the token in Authorization header
        @Body paymentData: HashMap<String, String>  // Send the payment data as a HashMap
    ): Call<VerifyPaymentResponse>

    @GET("mockTest/getAll")
    fun getMockTests(
        @Header("Authorization") authToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Call<MockTestResponse>

     @GET("blog/getAllBlogApp")
    fun getAllBlogApp(
        @Header("Authorization") authToken: String,

    ): Call<GetAllBlogAppResponse>

    @GET("blog/getBlogDetailsById")
    fun getBlogDetails(
        @Query("blog_id") blogId: String  // Pass the blog_id as a query parameter
    ): Call<BlogResponse>

    @GET("blog/getAll") // Base URL is already defined in RetrofitClient
    fun getBlogs(
        @Header("Authorization") token: String,
        @Query("blog_Category_id") categoryId: String?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Call<GetAllBlogAppResponse>

        @POST("user/logout")
        fun logoutUser(@Header(Constants.Authorization) token: String): Call<ApiResponse>
    @GET("mockTest/getAllQuestionById")
    fun getMockTestQuestions(@Header(Constants.Authorization) token: String,
        @Query("mockTest_id") mockTestId: String
    ): Call<QuestionComparisonResponse>

}


/*
    @FormUrlEncoded
    @POST("signInGoogle")
    fun userLoginWithGoogel(@FieldMap hm: HashMap<String, String?>): Call<LoginData?>?

    @GET(Const.getProfileApi)
    fun getProfileApi(@Header(Constants.Authorization) token: String?): Call<AllResponseModel?>?

    //@FormUrlEncoded
    @GET(Const.changePasswordApi)
    fun changePasswordApi(
        @Header(Constants.Authorization) token: String?,
        @QueryMap hm: HashMap<String, String?>
    ): Call<VrificationOtp?>?

    @Multipart
    @POST(Const.updateSalesProfile)
    fun updateSalesProfile(
        @Header(Constants.Authorization) token: String?,
        @PartMap hm: HashMap<String, RequestBody>,
        @Part profilePic: MultipartBody.Part
    ): Call<AllResponseModel?>?


    @POST(Const.sendOtp)
    fun forgotPassword(@Body body: RequestBody): Call<AllResponseModel>?


    @FormUrlEncoded
    @POST(Const.pushNotificationApi)
    fun pushNotificationApi(
        @FieldMap hm: HashMap<String, String?>,
        @Header(Constants.Authorization) token: String?
    ): Call<AllResponseModel?>?

//jhfjidhf
    @Multipart
    @POST(Const.signUp)
    fun signUp(
        @PartMap hm: HashMap<String, RequestBody>,
        @Part userImage: MultipartBody.Part,): Call<SignupResponse>

    @FormUrlEncoded
    @PUT(Const.verifyOtp)
    fun verifyOtp(@FieldMap hm: HashMap<String, String>): Call<VrificationOtp>

    @FormUrlEncoded
    @PUT(Const.verifyOtp)
    fun otpLogin(@FieldMap hm : HashMap<String, RequestBody>): Call<UserData>


*/




