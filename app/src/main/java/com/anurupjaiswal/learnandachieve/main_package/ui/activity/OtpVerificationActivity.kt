package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.anurupjaiswal.learnandachieve.R
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.ActivityOtpVerificationBinding
import com.anurupjaiswal.learnandachieve.model.VerifyOtpResponse

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpVerificationActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOtpVerificationBinding
    private var apiservice: ApiService? = null
    val activity:Activity =this@OtpVerificationActivity
 var  email :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        apiservice = RetrofitClient.client

        binding.lbVerity.setOnClickListener(this)
        val dataBundle = intent.extras
         email = dataBundle?.getString(Constants.Email).toString()

// Set the text of the TextView with the email
        binding.tvSubtitle.text = "Kindly check your $email inbox or spam folder for OTP"
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
        if (view == binding.lbVerity) {
            if (validateFields()) {
                otpVerificationApi()
            }
        }
    }



    private fun otpVerificationApi() {
        val params = mapOf(
            Constants.Email to email,
            Constants.otp to binding.PinView.text.toString(),
            "type" to "forgot-password"
        )
        apiservice?.verifyOtp(params)?.enqueue(object : retrofit2.Callback<VerifyOtpResponse> {


            override fun onResponse(
                call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>
            ) {
                Utils.toggleProgressBarAndText(true, binding.loading, binding.tvOtpVerification,binding.root)
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val verifyOtpResponse = response.body()
                        verifyOtpResponse?.let {
                            Utils.T(activity, it.message)
                            val bundle = Bundle()
                            bundle.putString(Constants.token,verifyOtpResponse.token)
                            bundle.putString(Constants.Email,email)
                            bundle.putString(Constants.Data,verifyOtpResponse.userData?.toString())
                            Utils.I(activity,ResetPasswordActivity::class.java,bundle)

                        }
                    } else if (response.code() == StatusCodeConstant.BAD_REQUEST) {


                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)


                        response.errorBody()?.let { errorBody ->
                            val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val displayMessage = message.error ?: "InValid OTP"


                                    Toast.makeText(activity, displayMessage, Toast.LENGTH_SHORT).show()
                            }
                    }else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        response.errorBody()?.let { errorBody ->
                            val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val displayMessage = message.message ?: "Unauthorized Access"

                            // Ensure message is non-empty before showing Toast
                            if (displayMessage.isNotEmpty()) {
                                    Toast.makeText(activity, displayMessage, Toast.LENGTH_SHORT).show()

                            }
                            Utils.UnAuthorizationToken(activity)
                        }
                    }



                    else if (response.code() == StatusCodeConstant.NOT_FOUND) {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)


                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                    Toast.makeText(activity, t.message ?: "Request failed Try Again Later", Toast.LENGTH_SHORT).show()
            }
        })
    }






}
