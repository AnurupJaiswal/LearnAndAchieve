package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.ValidationFragment
import com.anurupjaiswal.learnandachieve.databinding.FragmentOTPVerificationBinding
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.anurupjaiswal.learnandachieve.model.RegistrationVerifyOtpResponse
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPVerificationFragment : Fragment(), ValidationFragment {

    private var _binding: FragmentOTPVerificationBinding? = null
    private val binding get() = _binding!!

    private var apiservice: ApiService? = null

    private var countDownTimer: CountDownTimer? = null
 private  var token : String = ""
    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var dateOfBirth: String? = null
    private var gender: String? = null
    private var schoolName: String? = null
    private var className: String? = null
    private var classId: String? = null
    private var medium: String? = null
    private var email: String? = null
    private var mobile: String? = null
    private var addressLineOne: String? = null
    private var addressLineTwo: String? = null
    private var state: String? = null
    private var district: String? = null
    private var taluka: String? = null
    private var pincode: String? = null
    private var registerBy: String? = null
    private var referralCode: String? = null



     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOTPVerificationBinding.inflate(inflater, container, false)

                apiservice = RetrofitClient.client


         retrieveArguments()


        // Set up text change listener with validation and animation
        binding.PinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateFields() // Validate on each text change

            }
        })


         binding.tvResendOtp.setOnClickListener {

             resendOTPAPI()
         }


         return binding.root

    }

    override fun validateFields(): Boolean {
        val pinText = binding.PinView.text.toString()

        return when {
            pinText.isEmpty() -> {
                showError(getString(R.string.empty_error))
                false
            }

            pinText.length != 6 -> {
                showError(getString(R.string.invalid_pin_length_error))
                false
            }

            else -> {
                hideError()
                true
            }
        }
    }

    private fun showError(message: String) {
        binding.tvErrorName.text = message
        if (binding.tvErrorName.visibility == View.GONE) {
            binding.tvErrorName.visibility = View.VISIBLE
            val fadeIn = AlphaAnimation(0.0f, 1.0f)
            fadeIn.duration = 300
            binding.tvErrorName.startAnimation(fadeIn)
        }
    }

    private fun hideError() {
        if (binding.tvErrorName.visibility == View.VISIBLE) {
            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.duration = 300
            fadeOut.setAnimationListener(object :
                android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation) {
                    binding.tvErrorName.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
            })
            binding.tvErrorName.startAnimation(fadeOut)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }



    fun callApi(onResponse: (Boolean) -> Unit) {
        // Prepare the OTP request body
        val otpRequest = JsonObject().apply {
            addProperty("otp", binding.PinView.text.toString().trim())
            addProperty("type", "registration")
        }



        val authToken = "Bearer $token"


        // Call the API using Retrofit (asynchronous call with enqueue)
        apiservice?.verifyOtpRegestation(authToken, otpRequest)?.enqueue(object : Callback<RegistrationVerifyOtpResponse> {
            override fun onResponse(call: Call<RegistrationVerifyOtpResponse>, response: Response<RegistrationVerifyOtpResponse>) {
                if (response.isSuccessful) {
                    // Handle success response

                    Utils.T(requireContext(), "OTP verified successfully")
                    val verifyOtpResponse = response.body()
                    Log.d("OTPVerification", "Response: ${verifyOtpResponse?.message}")

                    onResponse(true)
                } else {
                    // Handle error response
                    val errorBody = response.errorBody()?.string()
                    Log.e("OTPVerification", "Error: $errorBody")
                    Toast.makeText(requireContext(), "OTP verification failed", Toast.LENGTH_SHORT).show()
                    // Call the onResponse with false indicating failure
                    onResponse(false)
                }
            }

            override fun onFailure(call: Call<RegistrationVerifyOtpResponse>, t: Throwable) {
                // Handle failure
                Log.e("OTPVerification", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
                // Call the onResponse with false indicating failure
                onResponse(false)
            }
        })
    }



    private fun startResendOtpTimer() {
        val totalTime = 60000L // 60 seconds
        val interval = 1000L // 1 second

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                val formattedTime = String.format("%02d : %02d", minutes, seconds)

                // Update the tvTimeRemaning TextView

                binding.tvTimeRemaning.visibility = View.VISIBLE

                binding.tvTimeRemaning.text = formattedTime
            }

            override fun onFinish() {
                binding.tvResendOtp.text = getString(R.string.resend_otp)
                binding.tvResendOtp.isClickable = true
                binding.tvResendOtp.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.primaryColor) // Enabled color
                )
                binding.tvTimeRemaning.visibility = View.GONE
                binding.tvTimeRemaning.text = "00 : 00" // Reset timer display
            }
        }
        countDownTimer?.start()
    }




    private fun retrieveArguments() {
        val dataBundle = arguments
        token = dataBundle?.getString("authToken").toString()
        firstName = dataBundle?.getString("firstName")
        middleName = dataBundle?.getString("middleName")
        lastName = dataBundle?.getString("lastName")
        dateOfBirth = dataBundle?.getString("dateOfBirth")
        gender = dataBundle?.getString("gender")
        schoolName = dataBundle?.getString("schoolName")
        className = dataBundle?.getString("className")
        classId = dataBundle?.getString("classId")
        medium = dataBundle?.getString("medium")
        email = dataBundle?.getString("email")
        mobile = dataBundle?.getString("mobile")
        addressLineOne = dataBundle?.getString("addressLineOne")
        addressLineTwo = dataBundle?.getString("addressLineTwo")
        state = dataBundle?.getString("state")
        district = dataBundle?.getString("district")
        taluka = dataBundle?.getString("taluka")
        pincode = dataBundle?.getString("pincode")
        registerBy = dataBundle?.getString("registeredBy")
        referralCode = dataBundle?.getString("referralCode")

        dataBundle?.keySet()?.forEach { key ->
            val value = dataBundle.getString(key)
            Log.d("OTPVerificationFragment", "Key: $key, Value: $value")
        }
    }


    private fun resendOTPAPI() {


        val jsonRequest = JsonObject().apply {
            addProperty("firstName", firstName)
            addProperty("middleName", middleName)
            addProperty("lastName", lastName)
            addProperty("dateOfBirth", dateOfBirth)
            addProperty("gender", gender)
            addProperty("schoolName", schoolName)
            addProperty("class", className)
            addProperty("classId", classId)
            addProperty("medium", medium)
            addProperty("email", email)
            addProperty("mobile", mobile)
            addProperty("addressLineOne", addressLineOne)
            addProperty("addressLineTwo", addressLineTwo)
            addProperty("state", state)
            addProperty("district", district)
            addProperty("taluka", taluka)
            addProperty("pincode", pincode)
            addProperty("registerBy", registerBy)
            addProperty("referralCode", referralCode)
        }

        Log.d("API Request", "Request Body: $jsonRequest")




        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaTypeOrNull())

        apiservice?.registerUser(requestBody)?.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    // Handle success
                    val signupResponse = response.body()
                    Log.d("API Success", "Response: ${signupResponse?.message}")

                     token = signupResponse?.token!!

                    startResendOtpTimer()
                    binding.tvResendOtp.isClickable = false
                    binding.tvResendOtp.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.gray) // Disabled color
                    )

                } else {
                    // Handle API error response
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Code: ${response.code()}, Error: $errorBody")



                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                // Handle failure
                Log.e("API Failure", "Error: ${t.message}")


                Utils.T(activity, "Something went wrong. Please try again.")
            }
        })


    }


}
