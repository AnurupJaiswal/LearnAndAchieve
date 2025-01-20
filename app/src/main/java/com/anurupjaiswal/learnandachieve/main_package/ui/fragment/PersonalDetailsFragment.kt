package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.R.anim.top_to_bottom
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.utilitytools.ValidationFragment
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.DatePicker
import com.anurupjaiswal.learnandachieve.databinding.FragmentPersonalDetailsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ClassSelectionAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.SelectionAdapter
import com.anurupjaiswal.learnandachieve.model.ClassData
import com.anurupjaiswal.learnandachieve.model.ClassResponse
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("UNREACHABLE_CODE")
class PersonalDetailsFragment : Fragment(), ValidationFragment {

    private lateinit var binding: FragmentPersonalDetailsBinding
    val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    private var apiservice: ApiService? = null
    private var selectedClassId: String? = null  // To store the selected class ID
    var classNames: List<String> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        apiservice = RetrofitClient.client

        setupPopups()
        getAllClasses()
        binding.etDateOfBirth.setOnClickListener {
            showDatePickerBottomSheet()
        }
        return binding.root
    }


    override fun validateFields(): Boolean {
        val errorValidationModels = mutableListOf<ValidationModel>()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etFirstName, binding.tvErrorFirstName
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etLastName, binding.tvErrorLastName
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etDateOfBirth, binding.tvErrorDateOfBirth
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etGender, binding.tvGender
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etSchoolName, binding.tvErrorSchoolName
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etMedium, binding.tvErrorMedium
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etClass, binding.tvErrorClass
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etRegesterdBY, binding.tvErrorRegesterdBY
            )
        )


        if (binding.etUniqueCode.visibility == View.VISIBLE) {
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.Empty, binding.etUniqueCode, binding.tvErrorUniqueCode
                )
            )
        }

        val validation = Validation.instance
        val resultReturn = validation?.CheckValidation(requireContext(), errorValidationModels)

        return if (resultReturn?.aBoolean == true) {
            true
        } else {
            resultReturn?.errorTextView?.apply {
                visibility = View.VISIBLE
                text = resultReturn.errorMessage ?: validation.errorMessage
                startAnimation(AnimationUtils.loadAnimation(context, top_to_bottom))
            }

            // If the error pertains to gender, class, or medium, remove focus from all EditTexts
            if (resultReturn?.errorTextView == binding.tvGender || resultReturn?.errorTextView == binding.tvErrorClass || resultReturn?.errorTextView == binding.tvErrorDateOfBirth || resultReturn?.errorTextView == binding.tvErrorMedium) {
                clearAllEditTextFocusWithoutCursor()
            } else {
                resultReturn?.errorTextView?.visibility = View.VISIBLE
                resultReturn?.errorTextView?.text =
                    resultReturn?.errorMessage ?: validation?.errorMessage
                validation?.EditTextPointer?.requestFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)

            }

            false
        }
    }


    private fun setupPopups() {
        binding.etGender.setOnClickListener {
            showSelectionPopup(binding.etGender, listOf("Male", "Female", "Other"))
        }
        binding.etClass.setOnClickListener {
            showSelectionPopup(binding.etClass, classNames)
        }
        binding.etMedium.setOnClickListener {
            showSelectionPopup(binding.etMedium, listOf("English", "Marathi", "Semi-English"))
        }


        binding.etRegesterdBY.setOnClickListener {
            showSelectionPopup(binding.etRegesterdBY, listOf("Self", "Coordinator"))
        }
        binding.etRegesterdBY.setOnClickListener {
            showSelectionPopup(
                binding.etRegesterdBY,
                listOf("Self", "Coordinator")
            ) { selectedText ->
                binding.etRegesterdBY.setText(selectedText)
                // Show coordinatorEditText if "Coordinator" is selected, else hide it
                binding.etUniqueCode.visibility = if (selectedText == "Coordinator") {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }






    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }


    private fun clearAllEditTextFocusWithoutCursor() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        listOf(
            binding.etFirstName,
            binding.etLastName,
            binding.etDateOfBirth,
            binding.etGender,
            binding.etSchoolName,
            binding.etMedium,
            binding.etClass,
            binding.etRegesterdBY
        ).filter { it.isFocused }.forEach { editText ->
                editText.clearFocus()
                editText.isCursorVisible = false
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

            }
    }


    private fun showDatePickerBottomSheet() {
        val picker = DatePicker(requireContext()) // Use requireContext() for fragment

        picker.show(requireActivity().window) // Use requireActivity() to access window
        picker.setOnClickOkButtonListener {
            val pickerView = picker.pickerView ?: return@setOnClickOkButtonListener
            val year = pickerView.year
            val month = pickerView.month
            val day = pickerView.day
            // Update selected date text
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, day)
            }.time
            binding.etDateOfBirth.setText(dateFormatter.format(selectedDate))  // Display selected date in TextView


            picker.hide()
        }
    }

    fun callApi(onResponse: (Boolean) -> Unit) {
        // Implement actual API call logic here
        onResponse(true)
    }

    private fun getAllClasses() {
        apiservice?.getAllClasses()?.enqueue(object : Callback<ClassResponse> {
            override fun onResponse(
                call: Call<ClassResponse>, response: retrofit2.Response<ClassResponse>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val classDataList = response.body()?.data // List<ClassData>
                        if (!classDataList.isNullOrEmpty()) {
                            classNames = classDataList.map { it.class_name }
                            classNames.forEach { className ->
                                Log.e("onResponse:", "Class Name: $className")
                            }

                            // Pass classDataList when showing the popup
                            binding.etClass.setOnClickListener {
                                showSelectionPopup(binding.etClass, classNames, classDataList)

                            }
                        } else {
                            Utils.T(requireContext(), "No classes available")
                        }
                    } else {
                        handleErrorResponse(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ClassResponse>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.T(activity, t.message)
                E("getMessage::" + t.message)
            }
        })
    }



    private fun handleErrorResponse(response: retrofit2.Response<ClassResponse>) {
        val message = Gson().fromJson(
            response.errorBody()?.charStream(), APIError::class.java
        )
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> Utils.T(activity, message.message)
            StatusCodeConstant.UNAUTHORIZED -> {
                Utils.T(activity, message.message)
                Utils.UnAuthorizationToken(requireContext())
            }
        }
    }

    fun getPersonalDetailsBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("firstName", binding.etFirstName.text?.toString())
        bundle.putString("middleName", binding.etMiddleName.text?.toString())
        bundle.putString("lastName", binding.etLastName.text?.toString())
        bundle.putString("schoolName", binding.etSchoolName.text?.toString())
        bundle.putString("medium", binding.etMedium.text?.toString())
        bundle.putString("uniqueCode", binding.etUniqueCode.text?.toString())
        bundle.putString("class", binding.etClass.text?.toString())

        // Check if etRegesterdBY.text is "self" and put "Student", otherwise use the text value
        val registeredBy = if (binding.etRegesterdBY.text?.toString().equals("self", ignoreCase = true)) {
            "Student"
        } else {
            binding.etRegesterdBY.text?.toString()
        }
        bundle.putString("registeredBy", registeredBy)

        bundle.putString("gender", binding.etGender.text?.toString())
        bundle.putString("classId", selectedClassId)
        bundle.putString("dateOfBirth", binding.etDateOfBirth.text?.toString())
        return bundle
    }


    private fun showSelectionPopup(
        editText: EditText,
        items: List<String>,
        classDataList: List<ClassData> = emptyList(),
        onItemSelected: (String, String) -> Unit = { _, _ -> }
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val heightInPixels = if (items.size > 3) {
            110.dpToPx()
        } else {
            LinearLayout.LayoutParams.WRAP_CONTENT
        }

        val popupWindow = PopupWindow(
            popupView, editText.width, heightInPixels, true
        ).apply {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.popup_background_with_shadow
                )
            )
            elevation = 10f
        }

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Use the class selection adapter for class data
        val adapter = if (classDataList.isNotEmpty()) {
            ClassSelectionAdapter(classDataList) { selectedClassName, classId ->
                editText.setText(selectedClassName)
                selectedClassId = classId // Save the selected class ID
                onItemSelected(selectedClassName, classId)
                popupWindow.dismiss()
            }
        } else {
            SelectionAdapter(items) { selectedText ->
                editText.setText(selectedText)
                onItemSelected(selectedText, "")
                popupWindow.dismiss()
            }
        }

        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = true

        val location = IntArray(2)
        editText.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1] + editText.height - 6

        popupWindow.showAtLocation(editText, Gravity.NO_GRAVITY, x, y)
    }
    private fun showSelectionPopup(
        editText: EditText,
        items: List<String>,
        onItemSelected: (String) -> Unit = {}
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val heightInPixels = if (items.size > 3) {
            110.dpToPx()
        } else {
            LinearLayout.LayoutParams.WRAP_CONTENT
        }

        val popupWindow = PopupWindow(
            popupView, editText.width, heightInPixels, true
        ).apply {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.popup_background_with_shadow
                )
            )
            elevation = 10f
        }

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SelectionAdapter(items) { selectedText ->
            editText.setText(selectedText)
            onItemSelected(selectedText)

            Log.d("PersonalDetailsFragment", "Selected Text: $selectedText")



            popupWindow.dismiss()
        }

        recyclerView.isNestedScrollingEnabled = true

        val location = IntArray(2)
        editText.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1] + editText.height - 6

        popupWindow.showAtLocation(editText, Gravity.NO_GRAVITY, x, y)
    }


}
