package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ActivityDeleteuserverifactionBinding
import com.anurupjaiswal.learnandachieve.databinding.ActivityOtpVerificationBinding
import com.anurupjaiswal.learnandachieve.model.ApiResponse
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.anurupjaiswal.learnandachieve.model.VerifyOtpResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteuserverifactionActivity : BaseActivity(),View.OnClickListener {

    private lateinit var binding: ActivityDeleteuserverifactionBinding
    private var apiservice: ApiService? = null
    private var countDownTimer: CountDownTimer? = null

    val activity: Activity =this@DeleteuserverifactionActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDeleteuserverifactionBinding.inflate(layoutInflater)


        init()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }


    private fun init() {
        apiservice = RetrofitClient.client
        binding.tvResendOtp.isClickable = true
binding.McvBack.setOnClickListener(this)
        binding.lbVerity.setOnClickListener(this)
binding.tvResendOtp.setOnClickListener(this)
// Set the text of the TextView with the email
        binding.tvSubtitle.text = "Kindly check your ${Utils.GetSession().email} inbox or spam folder for OTP"
        binding.PinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
            }
        })
    }



    private fun validateFields(): Boolean {
        val pinText = binding.PinView.text.toString()
        if (pinText.isEmpty() || pinText.length != 6) {
            val errorMessage = if (pinText.isEmpty()) getString(R.string.empty_error)
            else getString(R.string.invalid_pin_length_error)

            binding.tvError.text = errorMessage
            if (binding.tvError.visibility == View.GONE) {
                binding.tvError.visibility = View.VISIBLE
                val fadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 300 }
                binding.tvError.startAnimation(fadeIn)
            }

            // Request focus and show keyboard
            binding.PinView.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.PinView, InputMethodManager.SHOW_IMPLICIT)

            return false
        } else {
            if (binding.tvError.visibility == View.VISIBLE) {
                val fadeOut = AlphaAnimation(1.0f, 0.0f).apply { duration = 300 }
                fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation) {}
                    override fun onAnimationEnd(animation: android.view.animation.Animation) {
                        binding.tvError.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
                })
                binding.tvError.startAnimation(fadeOut)
            }
            return true
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.lbVerity -> {
                if (validateFields()) {
                    otpVerificationApi()
                }
            }
            binding.tvResendOtp -> {
                resendOTPAPI()
            }
            binding.McvBack ->{
                finish()
            }
        }
    }



    private fun otpVerificationApi() {
        val token = "Bearer ${Utils.GetSession().token}"
        val params = mapOf(
            Constants.Email to Utils.GetSession().email.toString(),
            Constants.otp to binding.PinView.text.toString(),
            "type" to "delete-user"
        )

        apiservice?.verifyOtp(token, params)?.enqueue(object : retrofit2.Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                // Toggle progress to "success" state
                Utils.toggleProgressBarAndText(true, binding.loading, binding.tvOtpVerification, binding.root)
                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            response.body()?.let { verifyOtpResponse ->
                                Utils.T(activity, verifyOtpResponse.message)
                                Utils.I_finish(activity, LoginActivity::class.java, null)
                            }
                        }
                        StatusCodeConstant.BAD_REQUEST -> {
                            Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification, binding.root)
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = apiError.error ?: "Invalid OTP"
                                Utils.T(activity, displayMessage)
                            }
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification, binding.root)
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = apiError.message ?: "Unauthorized Access"
                                if (displayMessage.isNotEmpty()) {
                                    Utils.T(activity, displayMessage)
                                }
                                Utils.E("otpVerificationApi UNAUTHORIZED: $displayMessage")
                                Utils.UnAuthorizationToken(activity)
                            }
                        }
                        StatusCodeConstant.NOT_FOUND -> {
                            Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification, binding.root)
                            Utils.E("otpVerificationApi: Not Found (404)")
                        }
                        else -> {
                            Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification, binding.root)
                            Utils.E("otpVerificationApi Error: ${response.code()} - ${response.errorBody()?.string()}")
                            Utils.T(activity, "Unexpected error: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification, binding.root)
                    Utils.E("otpVerificationApi Exception: ${e.message}")
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification, binding.root)
                Utils.E("otpVerificationApi onFailure: ${t.message}")
                Utils.T(activity, t.message ?: "Request failed. Try Again Later")
            }
        })
    }



    private fun resendOTPAPI() {

        val token = "Bearer ${Utils.GetSession().token}"

        // Make the API call to delete the user
        val apiService = RetrofitClient.client
        apiService.deleteUser(token).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                when (response.code()) {
                    StatusCodeConstant.OK -> {
                        // Successfully sent OTP
                        startResendOtpTimer()
                        binding.tvResendOtp.isClickable = false
                        binding.tvResendOtp.setTextColor(
                            ContextCompat.getColor(activity, R.color.gray) // Disabled color
                        )

                    }
                    StatusCodeConstant.NOT_FOUND -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "User not found"
                        Utils.T(activity, message)
                    }
                    StatusCodeConstant.UNAUTHORIZED -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "Unauthorized access"
                        Utils.T(activity, message)
                    }
                    StatusCodeConstant.BAD_REQUEST -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "Bad request"
                        Utils.T(activity, message)
                    }
                    else -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "An error occurred"
                        Utils.T(activity, message)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Handle request failure
                Utils.T(activity, "Request failed. Please try again.")
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
                    ContextCompat.getColor(activity, R.color.primaryColor) // Enabled color
                )
                binding.tvTimeRemaning.visibility = View.GONE
                binding.tvTimeRemaning.text = "00 : 00" // Reset timer display
            }
        }
        countDownTimer?.start()
    }


}