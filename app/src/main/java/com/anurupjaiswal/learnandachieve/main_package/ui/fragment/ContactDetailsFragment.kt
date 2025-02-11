package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.anurupjaiswal.learnandachieve.basic.utilitytools.ValidationFragment
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.FragmentContactDetailsBinding
import com.anurupjaiswal.learnandachieve.main_package.StateSelectionAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.DistrictSelectionAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.PincodeSelectionAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.TalukaSelectionAdapter
import com.anurupjaiswal.learnandachieve.model.DistrictApiResponse
import com.anurupjaiswal.learnandachieve.model.Districts
import com.anurupjaiswal.learnandachieve.model.Pincode
import com.anurupjaiswal.learnandachieve.model.PincodeApiResponse
import com.anurupjaiswal.learnandachieve.model.SignupResponse
import com.anurupjaiswal.learnandachieve.model.State
import com.anurupjaiswal.learnandachieve.model.StateApiResponse
import com.anurupjaiswal.learnandachieve.model.TalukaApiResponse
import com.anurupjaiswal.learnandachieve.model.Talukas
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContactDetailsFragment : Fragment(), ValidationFragment {

    private lateinit var binding: FragmentContactDetailsBinding
    private var apiService: ApiService? = null
    private var stateList: List<State> = emptyList()
    private var districtList: List<Districts> = emptyList()
    private var talukaList: List<Talukas> = emptyList()
    private var pincodeList: List<Pincode> = emptyList()

    private var selectedStateId: String? = null // To store the selected state ID
    private var selectedDistrictId: String? = null
    private var selectedTalukaId: String? = null
    private var selectedPincodeId: String? = null
    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
private var dateOfBirth: String? = null
    private var gender: String? = null
    private var schoolName: String? = null
    private var medium: String? = null
    private var className: String? = null
    private var classId: String? = null
    private var registeredBy: String? = null
    private var uniqueCode: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from arguments
        firstName = arguments?.getString("firstName")
        middleName = arguments?.getString("middleName")
      lastName = arguments?.getString("lastName")
       dateOfBirth = arguments?.getString("dateOfBirth")
      gender = arguments?.getString("gender")
       schoolName = arguments?.getString("schoolName")
       medium = arguments?.getString("medium")
       className = arguments?.getString("class")
       classId = arguments?.getString("classId")
    registeredBy = arguments?.getString("registeredBy")
     uniqueCode = arguments?.getString("uniqueCode")
        Log.d(
            "ContactDetailsFragment",
            "Received Data - First Name: $firstName, Middle Name: $middleName, Last Name: $lastName, " + "Date of Birth: $dateOfBirth, Gender: $gender, School Name: $schoolName, Medium: $medium, " + "Class Name: $className, Class ID: $classId, Registered By: $registeredBy, Unique Code: $uniqueCode"
        )


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using the binding
        binding = FragmentContactDetailsBinding.inflate(inflater, container, false)
        setupUI()

        apiService = RetrofitClient.client
        fetchStates()


        return binding.root
    }

    private fun setupUI() {
        // Setting up the State selection EditText
        binding.etState.setOnClickListener {
            // Check if the state list is loaded and not empty
            if (stateList.isNotEmpty()) {
                // Show the popup for state selection
                showSelectionPopup(binding.etState, stateList) { selectedName, selectedId ->
                    // Set the selected state name in the EditText
                    binding.etState.setText(selectedName)
                    selectedStateId = selectedId // Save the selected state ID
                    Log.d("State Selected ", "State: $selectedName, ID: $selectedId")

                    fetchDistricts(selectedId)

                }
            } else {
                // Display a message if states are not available
                Toast.makeText(
                    requireContext(),
                    "States are not available. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        // Setting up the District selection EditText
        binding.etDistrict.setOnClickListener {
            // Check if districts are loaded and not empty
            if (districtList.isNotEmpty()) {
                // Show the popup for district selection
                showDistrictSelectionPopup(
                    binding.etDistrict,
                    districtList
                ) { selectedName, selectedId ->
                    // Set the selected district name in the EditText
                    binding.etDistrict.setText(selectedName)
                    selectedDistrictId = selectedId // Save the selected district ID
                    Log.d(
                        "District Selected",
                        "District: $selectedName, ID: $selectedId , StateID: $selectedStateId"
                    )


                    fetchTalukas(selectedStateId ?: "", selectedId)
                }
            } else {
                // Display a message if districts are not available
                Toast.makeText(
                    requireContext(),
                    "Districts are not available for this state. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.etTaluka.setOnClickListener {
            if (talukaList.isNotEmpty()) {
                showTalukaSelectionPopup(
                    binding.etTaluka,
                    talukaList
                ) { selectedTalukaName, selectedTalukaId ->
                    binding.etTaluka.setText(selectedTalukaName)
                    this.selectedTalukaId = selectedTalukaId
                    fetchPincodeData(
                        selectedStateId ?: "",
                        selectedDistrictId ?: "",
                        selectedTalukaId
                    )
                }
            } else {
                Toast.makeText(requireContext(), "Talukas are not available.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.etPincode.setOnClickListener {
            if (pincodeList.isNotEmpty()) {
                showPincodeSelectionPopup(
                    binding.etPincode,
                    pincodeList
                ) { selectedPincode, selectedPincodeId ->
                    this.selectedPincodeId = selectedPincode
                }
            } else {
                Toast.makeText(requireContext(), "Talukas are not available.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    override fun validateFields(): Boolean {
        val errorValidationModels = mutableListOf<ValidationModel>()
        errorValidationModels.clear()

        // Add all fields for validation
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Email, binding.etEmail, binding.tvErrorEmail
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Phone, binding.etPhoneNumber, binding.tvErrorPhone
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etAddressLineOne, binding.tvErrorAddressLineOne
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etAddressLineTwo, binding.tvErrorAddressLineTwo
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty, binding.etState, binding.tvErrorState
            )
        )


        if (binding.etDistrict.visibility == View.VISIBLE) {
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.Empty, binding.etDistrict, binding.tvErrorDistrict
                )
            )
        }

        if (binding.etTaluka.visibility == View.VISIBLE) {
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.Empty, binding.etTaluka, binding.tvErrorTaluka
                )
            )
        }
        if (binding.etTaluka.visibility == View.VISIBLE) {
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.Empty, binding.etTaluka, binding.tvErrorTaluka
                )
            )
        }


        // Perform the validation check
        val validation = Validation.instance
        val resultReturn = validation?.CheckValidation(requireContext(), errorValidationModels)

        // Handle validation errors for fields
        if (resultReturn?.aBoolean == false) {
            resultReturn.errorTextView?.visibility = View.VISIBLE
            resultReturn.errorTextView?.text = resultReturn.errorMessage ?: validation.errorMessage
            validation.EditTextPointer?.requestFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(validation.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
            return false
        }

        // Final check for the checkbox
        if (!binding.checkboxTerms.isChecked) {
            Toast.makeText(
                requireContext(), "Please accept the terms and conditions.", Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
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


    private fun showDistrictSelectionPopup(
        editText: EditText,
        districtDataList: List<Districts>,
        onItemSelected: (String, String) -> Unit
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val heightInPixels = if (districtDataList.size > 3) {
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

        val adapter =
            DistrictSelectionAdapter(districtDataList) { selectedDistrictName, districtId ->
                editText.setText(selectedDistrictName) // Set the district name
                selectedDistrictId = districtId // Save the district ID
                onItemSelected(selectedDistrictName, districtId)
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


    // Show the Taluka selection popup
    private fun showTalukaSelectionPopup(
        editText: EditText,
        talukaDataList: List<Talukas>,
        onItemSelected: (String, String) -> Unit
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val heightInPixels = if (talukaDataList.size > 3) {
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

        val adapter = TalukaSelectionAdapter(talukaDataList) { selectedTalukaName, talukaId ->
            editText.setText(selectedTalukaName) // Set the taluka name
            selectedTalukaId = talukaId // Save the taluka ID
            onItemSelected(selectedTalukaName, talukaId)
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


    private fun showPincodeSelectionPopup(
        editText: EditText, pincodeDataList: List<Pincode>, onItemSelected: (String, String) -> Unit
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val heightInPixels = if (pincodeDataList.size > 3) {
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

        val adapter = PincodeSelectionAdapter(pincodeDataList) { selectedPincodeName, pincodeId ->
            editText.setText(selectedPincodeName) // Set the pincode name
            selectedPincodeId = pincodeId // Save the pincode ID
            onItemSelected(selectedPincodeName, pincodeId)
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


    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }


    fun callApi(onResponse: (Boolean, Bundle?) -> Unit) {
        val firstName =firstName
        val middleName = middleName
        val lastName = lastName
        val dateOfBirth = dateOfBirth
        val gender = gender
        val schoolName = schoolName
        val className = className
        val classId = classId
        val medium =medium
        val email = binding.etEmail.text.toString().trim()
        val mobile = binding.etPhoneNumber.text.toString().trim()
        val addressLineOne = binding.etAddressLineOne.text.toString().trim()
        val addressLineTwo = binding.etAddressLineTwo.text.toString().trim()
        val state = selectedStateId.toString().trim()
        val district = selectedDistrictId.toString().trim()
        val taluka = selectedTalukaId.toString().trim()
        val pincode = selectedPincodeId.toString().trim()
        val registerBy = registeredBy
        val referralCode = "".trim() // Referral code, if any

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

        // Create the request body
        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaTypeOrNull())

        // Make the API call
        apiService?.registerUser(requestBody)?.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.code() == StatusCodeConstant.OK) {
                    // Handle success
                    val signupResponse = response.body()
                    Log.d("API Success", "Response: ${signupResponse?.message}")

                    val combinedBundle = Bundle().apply {
                        putString("authToken", signupResponse?.token)
                        putString("email", email)
                        putString("mobile", mobile)
                        putString("addressLineOne", addressLineOne)
                        putString("addressLineTwo", addressLineTwo)
                        putString("state", state)
                        putString("district", district)
                        putString("taluka", taluka)
                        putString("pincode", pincode)
                    }

                    onResponse(true, combinedBundle)
                } else if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                    // Directly parse the error response
                    val errorBody = response.errorBody()?.string()
                    val apiError = try {
                        Gson().fromJson(errorBody, APIError::class.java)
                    } catch (e: JsonSyntaxException) {
                        Log.e("API Error Parsing", "Failed to parse error: ${e.message}")
                        null
                    }

                    Log.e("API Bad Request", "Code: ${response.code()}, Error: ${apiError?.message}")

                    Utils.T(activity, apiError?.message ?: "Invalid request. Please check your details and try again.")
                    onResponse(false, null)
                } else {
                    // Handle other API errors
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Code: ${response.code()}, Error: $errorBody")
                    Utils.T(activity, "Registration failed. Please try again.")
                    onResponse(false, null)
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                // Handle failure
                Log.e("API Failure", "Error: ${t.message}")
                Utils.T(activity, "Something went wrong. Please try again.")
                onResponse(false, null)
            }
        })
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


    private fun fetchDistricts(stateId: String) {
        binding.etDistrict.setText("")
        apiService?.getDistricts(offset = 0, limit = 10, searchQuery = "", stateId = stateId)
            ?.enqueue(object : Callback<DistrictApiResponse> {
                override fun onResponse(
                    call: Call<DistrictApiResponse>, response: Response<DistrictApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val districtApiResponse = response.body()
                        if (districtApiResponse?.data != null && districtApiResponse.data!!.isNotEmpty()) {
                            // Show the district EditText if districts are available
                            binding.etDistrict.visibility = View.VISIBLE
                            districtList = districtApiResponse.data ?: emptyList()
                        } else {
                            // Handle case where no districts are found
                            Toast.makeText(
                                requireContext(),
                                "No districts available.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.etDistrict.visibility = View.GONE
                            binding.etTaluka.visibility = View.GONE
                            binding.etPincode.visibility = View.GONE
                        }
                    } else {
                        Log.e("API Error", "Error fetching districts: ${response.code()}")
                        binding.etDistrict.visibility = View.GONE
                        binding.etTaluka.visibility = View.GONE
                        binding.etPincode.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<DistrictApiResponse>, t: Throwable) {
                    Log.e("API Error", "API call failed: ${t.message}")
                    binding.etDistrict.visibility = View.GONE
                    binding.etTaluka.visibility = View.GONE
                    binding.etPincode.visibility = View.GONE
                }
            })
    }

    // Fetch talukas based on selected state and district
    private fun fetchTalukas(stateId: String, districtId: String) {
        binding.etTaluka.setText("")
        apiService?.getTalukas(
            offset = 0,
            limit = 10,
            searchQuery = "",
            stateId = stateId,
            districtId = districtId
        )?.enqueue(object : Callback<TalukaApiResponse> {
                override fun onResponse(
                    call: Call<TalukaApiResponse>, response: Response<TalukaApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val talukaApiResponse = response.body()


                        if (talukaApiResponse?.data != null && talukaApiResponse.data!!.isNotEmpty()) {
                            // Update the list of talukas from the response
                            talukaList = talukaApiResponse.data!!

                            Log.d("Talukas Loaded", "Total Talukas: ${talukaList.size}")
                            // Make the Taluka EditText visible if talukas are available
                            binding.etTaluka.visibility = View.VISIBLE
                        } else {
                            // If no talukas are found, hide the EditText and show a toast
                            binding.etTaluka.visibility = View.GONE
                            binding.etPincode.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "No talukas found for this district.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("API Error", "Error fetching talukas: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<TalukaApiResponse>, t: Throwable) {
                    Log.e("API Error", "API call failed: ${t.message}")
                }
            })
    }


    // Fetch pincode data for selected state, district, and taluka
    private fun fetchPincodeData(stateId: String, districtId: String, talukaId: String) {
        binding.etPincode.setText("")
        apiService?.getPincodeData(
            offset = 0,
            limit = 10,
            searchQuery = "",
            stateId = stateId,
            districtId = districtId,
            talukaId = talukaId
        )?.enqueue(object : Callback<PincodeApiResponse> {
                override fun onResponse(
                    call: Call<PincodeApiResponse>, response: Response<PincodeApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val pincodeApiResponse = response.body()
                        if (pincodeApiResponse?.data != null && pincodeApiResponse.data!!.isNotEmpty()) {
                            // Update the list of pincodes from the response
                            pincodeList = pincodeApiResponse.data!!

                            Log.d("Pincodes Loaded", "Total Pincodes: ${pincodeList.size}")
                            // Make the Pincode EditText visible if pincodes are available
                            binding.etPincode.visibility = View.VISIBLE
                        } else {
                            // If no pincodes are found, hide the EditText and show a toast
                            binding.etPincode.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "No pincodes found for this taluka.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("API Error", "Error fetching pincodes: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PincodeApiResponse>, t: Throwable) {
                    Log.e("API Error", "API call failed: ${t.message}")
                }
            })
    }


}
