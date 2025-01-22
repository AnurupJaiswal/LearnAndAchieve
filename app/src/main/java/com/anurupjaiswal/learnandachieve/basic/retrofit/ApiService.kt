package com.anurupjaiswal.learnandachieve.basic.retrofit

import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.CartResponse
import com.anurupjaiswal.learnandachieve.model.ClassResponse
import com.anurupjaiswal.learnandachieve.model.DeleteCartResponse
import com.anurupjaiswal.learnandachieve.model.DistrictApiResponse
import com.anurupjaiswal.learnandachieve.model.ForgetPasswordResponse
import com.anurupjaiswal.learnandachieve.model.LoginData
import com.anurupjaiswal.learnandachieve.model.PackageDetailsResponse
import com.anurupjaiswal.learnandachieve.model.PackageResponse
import com.anurupjaiswal.learnandachieve.model.PincodeApiResponse
import com.anurupjaiswal.learnandachieve.model.RegistrationVerifyOtpResponse
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.anurupjaiswal.learnandachieve.model.StateApiResponse
import com.anurupjaiswal.learnandachieve.model.TalukaApiResponse
import com.anurupjaiswal.learnandachieve.model.VerifyOtpResponse
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {


    @FormUrlEncoded
    @POST(Const.USER_LOGIN)
    fun userLogin(@FieldMap hm: HashMap<String, String?>): Call<LoginData>?

    @GET(Const.GET_ALL_CLASSES)
    fun getAllClasses(): Call<ClassResponse>


    @GET(Const.GET_STATES) // This will append to the baseURL to form {{baseURL}}state/all
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
    fun verifyOtp(@Body params: Map<String, String?>): Call<VerifyOtpResponse>






    @POST(Const.RESET_PASSWORD)
    fun resetPassword(@Header(Constants.Authorization)
                      token: String,
                      @Body requestBody: Map<String, String>): Call<ForgetPasswordResponse>

    @GET(Const.PACKAGE_GET_ALL)
    fun getPackages(@Header(Constants.Authorization)
                    token: String,
                    @Query(Const.LIMIT) limit: Int,
        @Query(Const.OFFSET) offset: Int
    ):  Call<PackageResponse>



    @POST(Const.ADD_TO_CART)
    fun addToCart(
        @Header(Constants.Authorization) token: String?,   // Pass the token in the header
        @Body body: Map<String, String?>          // Use a Map for the request body
    ): Call<CartResponse>

    @GET(Const.GET_PACKAGE_DETAILS_BY_ID)
    fun getPackageDetails(
        @Header(Constants.Authorization) token: String?,   // Pass the token in the header
        @Query(Constants.PackageId) packageId: String?): Call<PackageDetailsResponse>

    @GET(Const.GET_ALL_CARDS)
    fun getCartData(@Header(Constants.Authorization) token: String): Call<AllCartResponse>


    @DELETE(Const.DELETE_CART_API)
    fun deleteCartItem(
        @Header("Authorization") authToken: String, // Authentication token for the request
        @Path("cart_id") cart_id: String // Cart item ID to be deleted
    ): Call<DeleteCartResponse>
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




