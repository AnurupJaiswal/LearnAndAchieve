package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.validation.ResultReturn
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.FragmentChangePasswordBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.ResetPasswordActivity
import com.anurupjaiswal.learnandachieve.model.ChangePasswordResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val errorValidationModels: MutableList<ValidationModel> = ArrayList()
    private var apiService: ApiService? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        apiService = RetrofitClient.client
        binding.lbChangePass.setOnClickListener { checkValidationTask() }

        setupPasswordToggle(binding.etNewPass)
        setupPasswordToggle(binding.etConfirmPass)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle(editText: EditText) {
        editText.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility(editText)
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
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_eye),
                null
            )
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_hide_eye),
                null
            )
        }
        editText.setSelection(editText.text.length) // Move cursor to the end
    }

    private fun checkValidationTask() {
        errorValidationModels.clear()

        // Add validations for the password fields
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etCurrentPass,
                binding.tvCurrentPassError
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PasswordStrong,
                binding.etNewPass,
                binding.tvNewError
            )
        )

        // Validation for confirming the new password only happens when the new password is valid
        if (binding.etNewPass.text.isNotEmpty()) {
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.PasswordMatch,
                    binding.etNewPass,
                    binding.etConfirmPass,
                    binding.tvConfirmPassError
                )
            )
        }

        val validation: Validation? = Validation.instance
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(requireActivity(), errorValidationModels)

        if (resultReturn?.aBoolean == true) {
            changePassword()
            Utils.toggleProgressBarAndText(true, binding.loading, binding.tvOtpVerification,binding.root)

        } else {
            resultReturn?.errorTextView?.visibility = View.VISIBLE
            resultReturn?.errorTextView?.text =
                resultReturn?.errorMessage ?: validation?.errorMessage

            validation?.EditTextPointer?.requestFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    fun changePassword() {
        val token = Utils.GetSession().token

        val currentPassword = binding.etCurrentPass.text.toString()
        val newPassword = binding.etNewPass.text.toString()
        val confirmPassword = binding.etConfirmPass.text.toString()
        val requestBody = HashMap<String, String>().apply {


            put(Constants.currentPassword, currentPassword)
            put(Constants.newPassword, newPassword)
            put(Constants.confirmPassword, confirmPassword)

        }

        apiService?.changePassword("Bearer $token", requestBody)
            ?.enqueue(object : retrofit2.Callback<ChangePasswordResponse> {


                override fun onResponse(
                    call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>
                ) {
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            val ChangePasswordResponse = response.body()
                            ChangePasswordResponse?.let {
                                Utils.T(activity, it.message)
                                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                                activity?.supportFragmentManager?.popBackStack()
                            }
                        } else if (response.code() == StatusCodeConstant.BAD_REQUEST) {

                            Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                            response.errorBody()?.let { errorBody ->
                                val message =
                                    Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = message.error

Utils.T(requireContext(),displayMessage)
                            }
                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                            response.errorBody()?.let { errorBody ->
                                val message =
                                    Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = message.message ?: "Unauthorized Access"

                                // Ensure message is non-empty before showing Toast
                                if (displayMessage.isNotEmpty()) {
                                    Utils.T(requireContext(),displayMessage)


                                }
                                Utils.UnAuthorizationToken(requireContext())
                            }
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    call.cancel()
                    t.printStackTrace()
                    Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                    Utils.T(requireContext(),t.message)

                }
            })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
