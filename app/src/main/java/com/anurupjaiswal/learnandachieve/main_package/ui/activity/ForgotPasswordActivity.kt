package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.ActivityForgotPasswordBinding
import com.anurupjaiswal.learnandachieve.model.ForgetPasswordResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val errorValidationModels = mutableListOf<ValidationModel>()
    private val activity: Activity = this@ForgotPasswordActivity
    private var apiservice: ApiService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiservice = RetrofitClient.client

        binding.lbSend.setOnClickListener(this)

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val validation = Validation.instance
                val validationResult = validation?.CheckValidation(
                    this@ForgotPasswordActivity,
                    listOf(
                        ValidationModel(
                            Validation.Type.Email,
                            binding.etEmail,
                            binding.tvErrorEmail
                        )
                    )
                )

                if (validationResult?.aBoolean == true) {
                    binding.tvErrorEmail.visibility = View.GONE
                } else {
                    binding.tvErrorEmail.visibility = View.VISIBLE
                    binding.tvErrorEmail.text =
                        validationResult?.errorMessage ?: validation?.errorMessage
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onClick(view: View) {
        if (view == binding.lbSend) {

            errorValidationModels.clear()
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.Email, binding.etEmail, binding.tvErrorEmail
                )
            )


            Log.e(TAG, "onClick: ${binding.etEmail.text}")
            val validation = Validation.instance
            val validationResult = validation?.CheckValidation(this, errorValidationModels)

            if (validationResult?.aBoolean == true) {

                Log.e("onClick: ", "forgot pass")

                forgotPassAPI()

            } else {
                Log.e("onClick: ", "forgot else")

                binding.tvErrorEmail.visibility = View.VISIBLE
                binding.tvErrorEmail.text =
                    validationResult?.errorMessage ?: validation?.errorMessage
                binding.tvErrorEmail.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.top_to_bottom)
                )

                validation?.EditTextPointer?.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
            }
        }


    }


    private fun forgotPassAPI() {

        val email = binding.etEmail.text.toString().trim()
        val json = "{\"email\":\"$email\"}"

        // Create the RequestBody
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        apiservice?.forgotPassword(requestBody)?.enqueue(object : Callback<ForgetPasswordResponse> {
            override fun onResponse(
                call: Call<ForgetPasswordResponse>,
                response: retrofit2.Response<ForgetPasswordResponse>
            ) {
                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            // Navigate to OtpVerificationActivity with email
                            Utils.T(activity, "Response: ${response.body()?.message}")
                            val bundle = Bundle().apply {
                                putString(Constants.Email, email)
                            }
                            Utils.I(activity, OtpVerificationActivity::class.java, bundle)
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            // Handle unauthorized error by calling the specified function
                            Utils.UnAuthorizationToken(activity)
                        }
                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = apiError.error ?: "Unknown error"
                                Utils.T(activity, "Response: $displayMessage")
                            }
                        }
                        else -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Something went wrong"
                                Utils.T(activity, "Response: $errorMessage")
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.T(activity, "Response: ${t.message ?: "Request failed"}")
                E("getMessage::" + t.message)
            }
        })
    }

}
