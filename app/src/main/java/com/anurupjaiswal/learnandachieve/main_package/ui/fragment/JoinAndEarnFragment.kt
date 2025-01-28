package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.DatePicker
import com.anurupjaiswal.learnandachieve.databinding.FragmentJoinAndEarnBinding
import com.anurupjaiswal.learnandachieve.main_package.StateSelectionAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.ExpandableCardAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.SelectionAdapter
import com.anurupjaiswal.learnandachieve.model.ApiResponse
import com.anurupjaiswal.learnandachieve.model.CardItem
import com.anurupjaiswal.learnandachieve.model.State
import com.anurupjaiswal.learnandachieve.model.StateApiResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class JoinAndEarnFragment : Fragment() ,View.OnClickListener {

    private var _binding: FragmentJoinAndEarnBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExpandableCardAdapter
    private var stateList: List<State> = emptyList()
    private var selectedStateId: String? = null // To store the selected state ID
    private val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    private var apiService: ApiService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize View Binding
        _binding = FragmentJoinAndEarnBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRcv()
        apiService = RetrofitClient.client
        fetchStates()
        binding.etDateOfBirth.setOnClickListener(this)
        binding.etQualification.setOnClickListener(this)
        binding.etState.setOnClickListener(this)
        binding.tvRegesterNow.setOnClickListener(this)
        binding.llTermsAndConditions.setOnClickListener(this)

        return view
    }


     private fun setupRcv()
     {
         val cardItems = listOf(
             CardItem(
                 "Your Choice, Your Path",
                 "Choose the category that resonates with your skills and interests. We trust you to make the best choice for your unique abilities. After selection and appointment by Learn and Achieve Edutech, you'll embark on an exciting journey in your chosen domain.",
                 Color.parseColor("#D6F6FF")
             ),
             CardItem(
                 "Compliance is Key",
                 "Every Coordinator must adhere to the terms and conditions of Learn and Achieve. We prioritize ethical conduct, transparency, and dedication to our mission of fostering quality education.",
                 Color.parseColor("#DCFFD3")
             ),
             CardItem(
                 "Rewards for Excellence",
                 "At Learn and Achieve Edutech, we believe in recognizing and rewarding your performance. You'll earn incentives that directly reflect your dedication and results. The harder you work, the more you can achieve.",
                 Color.parseColor("#D6F6FF")
             ),
             CardItem(
                 "Join Us, Embrace Opportunity",
                 "It's time to embrace an exciting opportunity with Learn and Achieve Edutech. We welcome both male and female candidates with a passion for education, a drive for success, and a commitment to making a difference in the lives of students across India",
                 Color.parseColor("#DCFFD3")
             ),
         )


         binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
         adapter = ExpandableCardAdapter(cardItems)
         binding.recyclerView.adapter = adapter

     }




    private fun validateFields(): Boolean {
        val errorValidationModels = mutableListOf<ValidationModel>()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etName,
                binding.tvErrorName
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Email,
                binding.etEmail,
                binding.tvErrorEmail
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Phone,
                binding.etMobile,
                binding.tvErrorMobile
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etDateOfBirth,
                binding.tvErrorDateOfBirth
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etQualification,
                binding.tvErrorQualification
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etAddressLineOne,
                binding.tvErrorAddressLineOne
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etAddressLineTwo,
                binding.tvErrorAddressLineTwo
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etState,
                binding.tvErrorState
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etDistrict,
                binding.tvErrorDistrict
            )
        )



        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etTaluka,
                binding.tvErrorTaluka
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PinCode,
                binding.etPincode,
                binding.tvErrorPincode
            )
        )




        val validation = Validation.instance
        val resultReturn = validation?.CheckValidation(requireContext(), errorValidationModels)

        return if (resultReturn?.aBoolean == true) {

            if (!binding.checkboxTerms.isChecked) {

                Toast.makeText(requireContext(), "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
                return false
            }else{

                addCoordinator()
                return true

            }


            return true
        } else {
            resultReturn?.errorTextView?.apply {
                visibility = View.VISIBLE
                text = resultReturn.errorMessage ?: validation.errorMessage
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.top_to_bottom))

                resultReturn.errorTextView?.visibility = View.VISIBLE
                resultReturn.errorTextView?.text = resultReturn.errorMessage ?: validation.errorMessage
                validation.EditTextPointer?.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(validation.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
            }

            false
        }
    }




    private fun showDatePickerBottomSheet() {
        val picker = DatePicker(requireContext())

        picker.show(requireActivity().window)
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


    private fun fetchStates() {
        apiService?.getStates()?.enqueue(object : Callback<StateApiResponse> {
            override fun onResponse(
                call: Call<StateApiResponse>,
                response: Response<StateApiResponse>
            ) {
                if (response.isSuccessful) {
                    val stateApiResponse = response.body()
                    if (stateApiResponse != null) {
                        stateList = stateApiResponse.data ?: emptyList()
                        Log.d("States Loaded", "Total States: ${stateList.size}")
                    }
                } else {
                    Log.e("API Error", "Response Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StateApiResponse>, t: Throwable) {
                Log.e("API Error", "API Call Failed: ${t.message}")
            }
        })
    }


    private fun showSelectionPopup(
        editText: EditText,
        stateDataList: List<State>,
        onItemSelected: (String, String) -> Unit
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val heightInPixels = if (stateDataList.size > 3) {
            110.dpToPx() // Adjust height of the popup based on the number of items
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

        // Use the StateSelectionAdapter for state data
        val adapter = StateSelectionAdapter(stateDataList) { selectedStateName, stateId ->
            editText.setText(selectedStateName) // Set the state name
            selectedStateId = stateId // Save the state ID
            E("State Selected State: $selectedStateName  ID: $stateId")
            onItemSelected(selectedStateName, stateId)
            popupWindow.dismiss()
        }

        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = true

        val location = IntArray(2)
        editText.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1] + editText.height - 6

        popupWindow.showAtLocation(editText, Gravity.NO_GRAVITY, x, y)
    }



    private fun showSelectionPopup(editText: EditText, items: List<String>, onItemSelected: (String) -> Unit = {}) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null) // Use a generic layout

        // Determine the height based on the number of items
        val heightInPixels = if (items.size > 3) {
            // If there are more than 3 items, set height to 75dp
            110.dpToPx()
        } else {
            // If there are 3 or fewer items, set height to wrap content
            LinearLayout.LayoutParams.WRAP_CONTENT
        }

        // Create the PopupWindow with the calculated height
        val popupWindow = PopupWindow(
            popupView,
            editText.width,
            heightInPixels,
            true
        ).apply {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.popup_background_with_shadow))
            elevation = 10f
        }

        // Set up RecyclerView inside the PopupWindow
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SelectionAdapter(items) { selectedText ->
            editText.setText(selectedText)
            onItemSelected(selectedText)
            popupWindow.dismiss()
        }

        // Enable scrolling inside the RecyclerView
        recyclerView.isNestedScrollingEnabled = true

        // Calculate position for the popup (below the EditText)
        val location = IntArray(2)
        editText.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1] + editText.height - 6

        // Show the popup at the calculated position
        popupWindow.showAtLocation(editText, Gravity.NO_GRAVITY, x, y)
    }


    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.etDateOfBirth -> showDatePickerBottomSheet()

            R.id.etQualification -> showSelectionPopup(
                binding.etQualification,
                listOf("Post Graduation", "Bachelor’s", "12th Standard")
            )
            R.id.etState ->

                if (stateList.isNotEmpty()) {
                    // Show the popup for state selection
                    showSelectionPopup(binding.etState, stateList) { selectedName, selectedId ->
                        // Set the selected state name in the EditText
                        binding.etState.setText(selectedName)
                        selectedStateId = selectedId // Save the selected state ID
                        Log.d("State Selected ", "State: $selectedName, ID: $selectedId")

                    }
                } else {
                    // Display a message if states are not available
                    Toast.makeText(
                        requireContext(),
                        "States are not available. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            R.id.tvRegesterNow -> validateFields()
            R.id.llTermsAndConditions -> findNavController().navigate(R.id.termsAndConditionsFragment, null)
        }
    }





    private fun addCoordinator() {
        val params = HashMap<String, Any>().apply {
            put(Constants.name, binding.etName.text.toString().trim())
            put(Constants.email, binding.etEmail.text.toString().trim())
            put(Constants.mobile, binding.etMobile.text.toString().trim())
            put(Constants.dateOfBirth,binding.etDateOfBirth.text.toString().trim())
            put(Constants.qualification, binding.etQualification.text.toString().trim())
            put(Constants.notifyEmail, true)
            put(Constants.addressLineOne,binding.etAddressLineOne.text.toString().trim())
            put(Constants.addressLineTwo, binding.etAddressLineTwo.text.toString().trim())
            put(Constants.state, selectedStateId!!)
            put(Constants.district, binding.etDistrict.text.toString().trim())
            put(Constants.taluka, binding.etTaluka.text.toString().trim())
            put(Constants.pincodeCoordinator, binding.etPincode.text.toString().trim())
        }

        // Get the authorization token
        val token = "Bearer ${Utils.GetSession().token}"

        // Make the API call
        val apiService = RetrofitClient.client
        apiService.addCoordinator(token, params).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                when (response.code()) {
                    StatusCodeConstant.OK -> {
                        val message = response.body()?.message ?: "Coordinator added successfully!"
                        Utils.T(requireContext(), message)
                        clearAllEditTexts(binding.root)

                    }
                    StatusCodeConstant.BAD_REQUEST -> {

                        response.errorBody()?.let { errorBody ->
                            val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val displayMessage = message.error
                            Utils.T(requireContext(), displayMessage)

                        }

                    }
                    StatusCodeConstant.UNAUTHORIZED -> {
                        response.errorBody()?.let { errorBody ->
                            val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val displayMessage = message.error

                            Utils.T(requireContext(), displayMessage)

                        }
                    }
                    else -> {
                        response.errorBody()?.let { errorBody ->
                            val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val displayMessage = message.error


                            Utils.T(requireContext(), displayMessage)

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Utils.T(requireContext(), "Request failed. Error: ${t.message}")
            }
        })
    }

    private fun clearAllEditTexts(parentView: ViewGroup) {
        for (i in 0 until parentView.childCount) {
            val child = parentView.getChildAt(i)
            if (child is EditText) {
                child.text?.clear()
            } else if (child is ViewGroup) {
                // Recursively clear EditTexts in nested ViewGroups
                clearAllEditTexts(child)
            }
        }
    }

}
