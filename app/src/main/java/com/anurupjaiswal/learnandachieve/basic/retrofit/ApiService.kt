package com.anurupjaiswal.learnandachieve.basic.retrofit

import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.ApiResponse
import com.anurupjaiswal.learnandachieve.model.BlogResponse
import com.anurupjaiswal.learnandachieve.model.CartResponse
import com.anurupjaiswal.learnandachieve.model.ChangePasswordResponse
import com.anurupjaiswal.learnandachieve.model.ClassResponse
import com.anurupjaiswal.learnandachieve.model.CreateOrderResponse
import com.anurupjaiswal.learnandachieve.model.DeleteCartResponse
import com.anurupjaiswal.learnandachieve.model.DistrictApiResponse
import com.anurupjaiswal.learnandachieve.model.FAQResponse
import com.anurupjaiswal.learnandachieve.model.ForgetPasswordResponse
import com.anurupjaiswal.learnandachieve.model.GetAllBlogAppResponse
import com.anurupjaiswal.learnandachieve.model.GetAllStudyMaterial
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.anurupjaiswal.learnandachieve.model.LoginData
import com.anurupjaiswal.learnandachieve.model.MockTestResponse
import com.anurupjaiswal.learnandachieve.model.ModuleResponse
import com.anurupjaiswal.learnandachieve.model.OrderHistoryResponse
import com.anurupjaiswal.learnandachieve.model.PackageDetailsResponse
import com.anurupjaiswal.learnandachieve.model.PackageResponse
import com.anurupjaiswal.learnandachieve.model.PerformanceSummaryResponse
import com.anurupjaiswal.learnandachieve.model.PincodeApiResponse
import com.anurupjaiswal.learnandachieve.model.QuestionComparisonResponse
import com.anurupjaiswal.learnandachieve.model.RegistrationVerifyOtpResponse
import com.anurupjaiswal.learnandachieve.model.ShowResultReponce
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.anurupjaiswal.learnandachieve.model.StateApiResponse
import com.anurupjaiswal.learnandachieve.model.SubmitMockTestRequest
import com.anurupjaiswal.learnandachieve.model.SubmitMockTestResponse
import com.anurupjaiswal.learnandachieve.model.TalukaApiResponse
import com.anurupjaiswal.learnandachieve.model.TermsConditionsResponse
import com.anurupjaiswal.learnandachieve.model.TopicResponse
import com.anurupjaiswal.learnandachieve.model.VerifyOtpResponse
import com.anurupjaiswal.learnandachieve.model.VerifyPaymentResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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


    @POST(Const.SUBMIT_MOCK_TEST)
    fun submitMockTest(
        @Header(Constants.Authorization) token: String, @Body request: SubmitMockTestRequest
    ): Call<SubmitMockTestResponse>


    @FormUrlEncoded
    @POST(Const.USER_LOGIN)
    fun userLogin(
        @FieldMap hm: HashMap<String, String?>
    ): Call<LoginData>?

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
    fun verifyOtp(
        @Header(Constants.Authorization) authToken: String, @Body params: Map<String, String?>
    ): Call<VerifyOtpResponse>


    @POST(Const.RESET_PASSWORD)
    fun resetPassword(
        @Header(Constants.Authorization) token: String, @Body requestBody: Map<String, String>
    ): Call<ForgetPasswordResponse>

    @GET(Const.PACKAGE_GET_ALL)
    fun getPackages(
        @Header(Constants.Authorization) token: String,
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

    @GET(Const.GET_ALL_TOPIC)
    fun getAllTopics(
        @Header(Constants.Authorization) authorization: String,
        @Query(Constants.moduleId) moduleId: String
    ): Call<TopicResponse>

    @GET(Const.GET_USER_DETAILS)
    fun getUserDetails(
        @Header(Constants.Authorization) authorization: String
    ): Call<GetUserResponse>

    @GET(Const.GET_ODER_ALL)
    fun getOrderHistory(
        @Header(Constants.Authorization) authorization: String
    ): Call<OrderHistoryResponse>


    @POST(Const.CHANGE_PASSWORD)
    fun changePassword(
        @Header(Constants.Authorization) token: String, @Body body: HashMap<String, String>
    ): Call<ChangePasswordResponse>

    @Multipart
    @POST(Const.UPDATE_PROFILE)
    fun updateProfile(
        @Header(Constants.Authorization) token: String,
        @Part profilePicture: MultipartBody.Part // Profile picture
    ): Call<GetUserResponse>

    @DELETE(Const.DELETE_PROFILE_PICTURE)
    fun deleteProfilePicture(
        @Header(Constants.Authorization) token: String,
    ): Call<ApiResponse>

    @POST(Const.DELETE_USER)
    fun deleteUser(@Header(Constants.Authorization) token: String): Call<ApiResponse>


    @POST(Const.ADD_COORDINATOR)
    fun addCoordinator(
        @Header(Constants.Authorization) token: String, @Body params: HashMap<String, Any>
    ): Call<ApiResponse>

    @GET(Const.GET_TERMS_CONDITIONS)
    fun getTermsConditions(): Call<TermsConditionsResponse>

    @GET(Const.PRIVACY_POLICY)
    fun getPrivacyPolicy(): Call<TermsConditionsResponse>

    @GET(Const.CANCELLATION_POLICY)
    fun getCancellationPolicy(): Call<TermsConditionsResponse>

    @GET("user/getAboutUsPageContent")
    fun getAboutUsPageContent(): Call<TermsConditionsResponse>

    @GET(Const.FAQ_ALL)
    fun getCategories(
        @Query(Const.LIMIT) limit: Int, @Query(Const.OFFSET) offset: Int
    ): Call<FAQResponse>

    @GET(Const.FAQ_ALL)
    fun getFAQsByCategory(
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.OFFSET) offset: Int,
        @Query(Constants.faq_Category_id) categoryId: String
    ): Call<FAQResponse>


    @POST(Const.CREATE_ORDER)
    fun createOrder(
        @Header(Constants.Authorization) token: String, @Body paymentData: HashMap<String, String>
    ): Call<CreateOrderResponse>


    // Endpoint for verifying payment
    @POST(Const.VERIFY_PAYMENT)
    fun verifyPayment(
        @Header(Constants.Authorization) token: String,
        @Body paymentData: HashMap<String, String>  // Send the payment data as a HashMap
    ): Call<VerifyPaymentResponse>

    @GET(Const.MOCK_TEST_ALL)
    fun getMockTests(
        @Header(Constants.Authorization) token: String,
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.OFFSET) offset: Int
    ): Call<MockTestResponse>

    @GET(Const.ALL_BLOG_APP)
    fun getAllBlogApp(
        @Header(Constants.Authorization) token: String,

        ): Call<GetAllBlogAppResponse>

    @GET(Const.BLOG_DETAILS_BY_ID)
    fun getBlogDetails(
        @Query(Constants.blogId) blogId: String  // Pass the blog_id as a query parameter
    ): Call<BlogResponse>

    @GET(Const.BLOG_GET_ALL)
    fun getBlogs(
        @Header(Constants.Authorization) token: String,
        @Query(Constants.blog_Category_id) categoryId: String?,
        @Query(Const.LIMIT) limit: Int,
        @Query(Const.OFFSET) offset: Int
    ): Call<GetAllBlogAppResponse>

    @POST("user/logout")
    fun logoutUser(@Header(Constants.Authorization) token: String): Call<ApiResponse>

    @GET(Const.GET_MOCK_TEST_QUESTIONS)
    fun getMockTestQuestions(
        @Header(Constants.Authorization) token: String,
        @Query(Constants.mockTest_id) mockTestId: String,
    ): Call<QuestionComparisonResponse>

    @GET(Const.SHOW_RESULT)
    fun getShowResults(
        @Header(Constants.Authorization) token: String,
        @Query(Constants.mockTest_id) mockTestId: String,
        @Query(Constants.order_id) orderId: String
    ): Call<ShowResultReponce>


    @GET(Const.VIEW_RESULT)
    fun getPerformanceSummary(
        @Header(Constants.Authorization) token: String,
        @Query(Constants.mockTest_id) mockTestId: String,
        @Query("mockTestSubmissions_id") mockTestSubmissionsId: String,
        @Query(Constants.subjectId) subjectId: String? = null
    ): Call<PerformanceSummaryResponse>
}







