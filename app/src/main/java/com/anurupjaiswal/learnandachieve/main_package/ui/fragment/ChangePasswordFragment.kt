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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.validation.ResultReturn
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val errorValidationModels: MutableList<ValidationModel> = ArrayList()

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

        initListeners()
    }

    private fun initListeners() {
        binding.lbChangePass.setOnClickListener { checkValidationTask() }

        // You can remove the afterTextChanged listeners if you want validation to be done only on submit
        binding.etNewPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Optional: You could add a debounce mechanism if needed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etConfirmPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Optional: You could add a debounce mechanism if needed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                null, null, ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_eye), null
            )
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(requireContext(), R.drawable.ic_hide_eye), null
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
        val resultReturn: ResultReturn? = validation?.CheckValidation(requireActivity(), errorValidationModels)

        if (resultReturn?.aBoolean == true) {
            // Successful validation logic, proceed to next action (e.g., navigate or save)
        } else {
            resultReturn?.errorTextView?.visibility = View.VISIBLE
            resultReturn?.errorTextView?.text = resultReturn?.errorMessage ?: validation?.errorMessage

            validation?.EditTextPointer?.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
