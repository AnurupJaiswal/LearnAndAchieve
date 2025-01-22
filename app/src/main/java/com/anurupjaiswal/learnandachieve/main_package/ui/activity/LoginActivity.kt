package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.User
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.SavedData
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.validation.ResultReturn
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.ActivityLoginBinding
import com.anurupjaiswal.learnandachieve.model.LoginData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var errorValidationModels: MutableList<ValidationModel> = ArrayList()
    private val activity: Activity = this@LoginActivity
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    private var apiservice: ApiService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        apiservice = RetrofitClient.client

        binding.mcvLogin.setOnClickListener(this)
        binding.rlDontHaveAccount.setOnClickListener(this)
        binding.tvForgotPass.setOnClickListener(this)

        textChangeListener()
        binding.etPassword.isHapticFeedbackEnabled = false

        binding.etPassword.setOnTouchListener { _, event ->
            val drawableEnd = 2 // Index for the end drawable (right icon)
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.mcvLogin -> {
                checkValidationTask()
            }

            R.id.rlDontHaveAccount -> {
                Utils.I(activity, SignUpActivity::class.java, null)
            }

            R.id.tvForgotPass -> {
                Utils.I(activity, ForgotPasswordActivity::class.java, null)
            }
        }
    }


    private fun validateField(type: Validation.Type, editText: EditText, errorTextView: TextView) {
        val validation = Validation.instance
        val validationModel = ValidationModel(type, editText, errorTextView)
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(activity, listOf(validationModel))

        if (resultReturn?.aBoolean == true) {
            errorTextView.visibility = View.GONE
        } else {
            resultReturn?.errorTextView?.visibility = View.VISIBLE
            resultReturn?.errorTextView?.text =
                resultReturn?.errorMessage ?: validation?.errorMessage
            validation?.EditTextPointer?.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun checkValidationTask() {
        errorValidationModels.clear()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Email, binding.etEmail, binding.tvErrorEmail
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PasswordLength, binding.etPassword, binding.tvErrorPass
            )
        )

        val validation: Validation? = Validation.instance
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(activity, errorValidationModels)

        if (resultReturn?.aBoolean == true) {
            userLogin()
        } else {
            resultReturn?.errorTextView?.visibility = View.VISIBLE
            resultReturn?.errorTextView?.text =
                resultReturn?.errorMessage ?: validation?.errorMessage
            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_to_bottom)
            resultReturn?.errorTextView?.startAnimation(animation)
            validation?.EditTextPointer?.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password and change icon
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_hide_eye, 0
            )
        } else {
            // Show password and change icon
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_show_eye, 0
            )
        }
        // Move cursor to the end of the text
        binding.etPassword.setSelection(binding.etPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }


    private fun textChangeListener() {
        // Add TextWatcher for real-time validation
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateField(Validation.Type.Email, binding.etEmail, binding.tvErrorEmail)
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateField(
                    Validation.Type.PasswordLength, binding.etPassword, binding.tvErrorPass
                )
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun userLogin() {
        Utils.toggleProgressBarAndText(true, binding.loading, binding.tvLogIN,binding.root)

        val fcmid = SavedData.getFirebaseToken()
        val hm = HashMap<String, String?>()

        hm[Constants.email] = binding.etEmail.text.toString().trim()
        hm[Constants.password] = binding.etPassword.text.toString().trim()
        //hm[Constants.fcmId] = fcmid
        E("" + binding.etEmail.text.toString())
        E("" + binding.etPassword.text.toString())
        apiservice?.userLogin(hm)?.enqueue(object : retrofit2.Callback<LoginData> {


            override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvLogIN,binding.root)
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val userModel = response.body()
                        if (userModel != null) {
                            val user = userModel.user
                            if (user != null) {
                                // Set token manually
                                user.token = userModel.token

                                // Log data for verification
                                E("User ID: ${user._id}")
                                E("Token: ${user.token}")

                                // Insert into local database
                                UserDataHelper.instance.insertData(user)
                                E("User data inserted into local database successfully.")
                            }

                            // Navigate to dashboard
                            Utils.I_clear(activity, DashboardActivity::class.java, null)
                        }
                    } else {
                        handleErrorResponse(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }




            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                call.cancel()
                t.printStackTrace()

                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvLogIN,binding.root)

                Utils.T(activity, t.message)
                E("getMessage::" + t.message)
            }
        })
    }

    private fun handleErrorResponse(response: Response<LoginData>) {
        if (response.code() == StatusCodeConstant.BAD_REQUEST || response.code() == StatusCodeConstant.UNAUTHORIZED) {
            assert(response.errorBody() != null)
            val message = Gson().fromJson(
                response.errorBody()!!.charStream(),
                APIError::class.java
            )
            Utils.T(activity, message.message)
            if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                Utils.UnAuthorizationToken(activity)
            }
        }
    }

}
