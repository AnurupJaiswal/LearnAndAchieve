package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.validation.ResultReturn
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.ActivityResetPasswordBinding
import com.anurupjaiswal.learnandachieve.model.ForgetPasswordResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class ResetPasswordActivity : BaseActivity(), View.OnClickListener {


    private var errorValidationModels: MutableList<ValidationModel> = ArrayList()
    private var apiservice: ApiService? = null
    private val activity: Activity = this@ResetPasswordActivity
var token :String? =null

    lateinit var binding: ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        init()


    }


    @SuppressLint("ClickableViewAccessibility")


    fun init() {

        apiservice = RetrofitClient.client

        binding.lbRest.setOnClickListener(this)

        val  data  = intent.extras
        token = data?.getString(Constants.token)

        binding.etNewPass.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etNewPass.right - binding.etNewPass.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility(binding.etNewPass)
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.etComPass.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etComPass.right - binding.etComPass.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility(binding.etComPass)
                    return@setOnTouchListener true
                }
            }
            false
        }

    }


    private fun togglePasswordVisibility(editText: EditText) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(this, R.drawable.ic_show_eye), null
            )

        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(this, R.drawable.ic_hide_eye), null
            )

        }
        editText.setSelection(editText.text.length) // Move cursor to the end
    }

    override fun onClick(view: View?) {

        if (view == binding.lbRest) {
            checkValidationTask()
        }

    }


    private fun checkValidationTask() {
        errorValidationModels.clear()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PasswordStrong, binding.etNewPass, binding.tvNewPassError
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PasswordMatch,
                binding.etNewPass,
                binding.etComPass,
                binding.tvComPassError
            )
        )

        val validation: Validation? = Validation.instance
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(activity, errorValidationModels)

        if (resultReturn?.aBoolean == true) {

            otpResetPassApi()
        } else {
            resultReturn?.errorTextView?.apply {
                visibility = View.VISIBLE
                text = resultReturn.errorMessage ?: validation.errorMessage
            }

            // Focus on the invalid EditText only if the field is empty or has an invalid value
            if (validation?.EditTextPointer?.text.isNullOrBlank() || resultReturn?.errorMessage != null) {
                validation?.EditTextPointer?.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun otpResetPassApi() {


        val requestBody = mapOf(Constants.newPassword to binding.etComPass.text.toString().trim())


         val authToken = "Bearer $token"

        apiservice?.resetPassword(authToken, requestBody)
            ?.enqueue(object : retrofit2.Callback<ForgetPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgetPasswordResponse>, response: Response<ForgetPasswordResponse>
                ) {
                    Utils.toggleProgressBarAndText(true, binding.loading, binding.tvOtpVerification)
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            val resetPasswordResponse = response.body()
                            resetPasswordResponse?.let {

                                Utils.T(this@ResetPasswordActivity,"Reset Successfully")
                                Utils.I_clear(activity, LoginActivity::class.java,null)
                            }
                        } else if (response.code() == StatusCodeConstant.BAD_REQUEST) {


                            Utils.toggleProgressBarAndText(
                                false, binding.loading, binding.tvOtpVerification
                            )


                            response.errorBody()?.let { errorBody ->
                                val message =
                                    Gson().fromJson(errorBody.charStream(), APIError::class.java)
                        //        val displayMessage = message.error ?: "InValid OTP"
                                //Toast.makeText(activity, displayMessage, Toast.LENGTH_SHORT).show()
                            }

                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            Utils.toggleProgressBarAndText(
                                false, binding.loading, binding.tvOtpVerification
                            )

                            response.errorBody()?.let { errorBody ->
                                val message =
                                    Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = message.message ?: "Unauthorized Access"

                                // Ensure message is non-empty before showing Toast
                                if (displayMessage.isNotEmpty()) {
                                    Toast.makeText(activity, displayMessage, Toast.LENGTH_SHORT)
                                        .show()

                                }
                                Utils.UnAuthorizationToken(activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable) {
                    call.cancel()
                    t.printStackTrace()
                    Utils.toggleProgressBarAndText(
                        false, binding.loading, binding.tvOtpVerification
                    )

                    Toast.makeText(
                        activity, t.message ?: "Request failed Try Again Later", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


}