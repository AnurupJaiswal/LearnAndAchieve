package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.User
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentProfileDetailsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.DetailsAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.PackageDetailsResponse
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileDetailsFragment : Fragment(R.layout.fragment_profile_details) {

    private var _binding: FragmentProfileDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var personalDetailsTab: TextView
    private lateinit var contactDetailsTab: TextView
    private lateinit var personalDetails: Map<String, String?>
    private lateinit var contactDetails: Map<String, String?>
    private val TAG = "ProfileDetailsFragment"
    private lateinit var detailsAdapter: DetailsAdapter

    // Launchers for Camera and Gallery Intents
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) { // Corrected here
                val photo = result.data?.extras?.get("data") as? Bitmap
                if (photo != null) {
                    binding.ivProfile.setImageBitmap(photo)
                    saveImage(photo)
                } else {
                    Log.e(TAG, "Camera returned null bitmap.")
                }
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) { // Corrected here
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val photo = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                    binding.ivProfile.setImageBitmap(photo)
                } else {
                    Log.e(TAG, "Gallery returned null URI.")
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileDetailsBinding.bind(view)

        setupUI()
    }

    private fun setupUI() {
        try {


            getUserDetails(Utils.GetSession().token!!)

            Utils.Picasso(
                Utils.GetSession().profilePicture.toString(),
                binding.ivProfile,
                R.drawable.ic_profile
            )
            personalDetailsTab = binding.personalDetailsTab
            contactDetailsTab = binding.contactDetailsTab
            binding.profileDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            detailsAdapter = DetailsAdapter(emptyMap()) // Start with empty data
            binding.profileDetailsRecyclerView.adapter = detailsAdapter

            val user = Utils.GetSession()// Assuming fetching the first user


            personalDetails = mapOf(
                "First Name" to user.firstName,
                "Middle Name" to user.middleName,
                "Last Name" to user.lastName,
                "Date Of Birth" to user.dateOfBirth,
                "Gender" to user.gender,
                "School Name" to user.schoolName,
                "Medium" to user.medium,
                "Class" to user.className,
                "Register By" to user.registerBy
            ).filterValues { !it.isNullOrEmpty() && it != "null" } // Remove null, empty, or "null" string values





            contactDetails = mapOf(
                "Email" to user.email,
                "Mobile" to user.mobile?.toString(),
                "Address Line -1" to user.addressLineOne,
                "Address Line -2" to user.addressLineTwo,
                "State" to user.state,
                "District" to user.district,
                "Taluka" to user.taluka
            ).filterValues { !it.isNullOrEmpty() && it != "null" } // Remove null, empty, or "null" string values

            Utils.Picasso(
                Utils.GetSession().profilePicture.toString(),
                binding.ivProfile,
                R.drawable.ic_profile
            )
            Log.d(TAG, "Personal Details: $personalDetails")
            Log.d(TAG, "Contact Details: $contactDetails")
            updateTabUI(isPersonalDetailsSelected = true)

            // Tab click listeners
            personalDetailsTab.setOnClickListener {
                updateTabUI(isPersonalDetailsSelected = true)
            }

            contactDetailsTab.setOnClickListener {
                updateTabUI(isPersonalDetailsSelected = false)
            }

            binding.McvEditPencil.setOnClickListener {
                showProfileOptionsBottomSheet()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupUI: ${e.message}", e)
        }
    }

    private fun updateTabUI(isPersonalDetailsSelected: Boolean) {
        try {
            if (isPersonalDetailsSelected) {
                personalDetailsTab.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.toggle_selected)
                personalDetailsTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                contactDetailsTab.background = null
                contactDetailsTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                detailsAdapter.updateData(personalDetails)

            } else {
                contactDetailsTab.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.toggle_selected)
                contactDetailsTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                personalDetailsTab.background = null
                personalDetailsTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))

                detailsAdapter.updateData(contactDetails)

            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateTabUI: ${e.message}", e)
        }
    }



    private fun   showCameraGalleryOptionsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_camera_gallary, null)

        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.findViewById<View>(R.id.tvCamera).setOnClickListener {
            bottomSheetDialog.dismiss()
            handlePermissionsForCamera()
        }

        bottomSheetView.findViewById<View>(R.id.tvGallery).setOnClickListener {
            bottomSheetDialog.dismiss()
            handlePermissionsForGallery()
        }

        bottomSheetDialog.show()
    }

    private fun handlePermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun handlePermissionsForGallery() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissions.any { ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED }) {
            requestPermissions(permissions.toTypedArray(), GALLERY_PERMISSION_CODE)
        } else {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode in listOf(CAMERA_PERMISSION_CODE, GALLERY_PERMISSION_CODE)) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                if (requestCode == CAMERA_PERMISSION_CODE) openCamera() else openGallery()
            } else {
                Toast.makeText(requireContext(), "Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun saveImage(bitmap: Bitmap?) {
        bitmap?.let {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
            val fileName = "IMG_$timeStamp.jpg"
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, fileName)
            FileOutputStream(file).use { fos ->
                it.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showProfileOptionsBottomSheet() {
        val profileOptionsBottomSheetDialog = BottomSheetDialog(requireContext())
        val profileOptionsView = layoutInflater.inflate(R.layout.bottom_sheet_profile_options, null)

        profileOptionsBottomSheetDialog.setContentView(profileOptionsView)

        // Handle "Change Profile" click
        profileOptionsView.findViewById<View>(R.id.rlChangeProfile).setOnClickListener {
            profileOptionsBottomSheetDialog.dismiss() // Close this sheet
            showCameraGalleryOptionsBottomSheet() // Open next sheet
        }

        // Handle "Delete" click
        profileOptionsView.findViewById<View>(R.id.rlDeleteProfile).setOnClickListener {
            profileOptionsBottomSheetDialog.dismiss() // Close this sheet
            deleteProfileImage() // Handle profile deletion
        }

        profileOptionsBottomSheetDialog.show()
    }

    private fun deleteProfileImage() {
        // Reset profile image to default (or remove it)
        binding.ivProfile.setImageResource(R.drawable.dummy)
        Toast.makeText(requireContext(), "Profile image deleted", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val GALLERY_PERMISSION_CODE = 101
    }


    private fun getUserDetails(authToken: String) {
        val apiService = RetrofitClient.client

        val tokenb = "Bearer $authToken"
        apiService.getUserDetails(tokenb).enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                // Hide loading state
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val userModel = response.body()

                        if (userModel?.user != null) {
                            val getUser = userModel.user

                            E("User ID: ${getUser.user_id}")
                            E("Full API Response: ${response.body()}")

                            // Convert GetUser data to User object
                            val userData = User().apply {
                                _id = getUser.user_id
                                token = authToken
                                firstName = getUser.firstName
                                middleName = getUser.middleName
                                lastName = getUser.lastName
                                dateOfBirth = getUser.dateOfBirth
                                gender = getUser.gender
                                schoolName = getUser.schoolName
                                medium = getUser.medium
                                classId = getUser.class_id
                                profilePicture = getUser.profilePicture
                                registerBy = getUser.registerBy
                                referralCode = getUser.referralCode
                                email = getUser.email
                                mobile = getUser.mobile
                                addressLineOne = getUser.addressLineOne
                                addressLineTwo = getUser.addressLineTwo
                                state = getUser.state
                                district = getUser.district
                                taluka = getUser.taluka
                                createdDate = getUser.created_date
                                updatedDate = getUser.updated_date
                                className = getUser.class_name
                            }

                            // Insert or update into the local database
                            UserDataHelper.instance.insertData(userData)
                            E("User data inserted into local database successfully.")
                        } else {
                            // Log or handle case where 'getUser' is null
                            E("Error: 'getUser' is null in response.")
                        }

                    } else {
                        handleGetUserResponseApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(context, "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                t.printStackTrace()
                Utils.T(context, t.message ?: "Request failed. Please try again later.")
            }
        })



    }


    private fun handleGetUserResponseApiError(response: Response<GetUserResponse>) {
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.error ?: "Invalid Request"
                    Utils.T(requireContext(), displayMessage)
                }
            }
            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Unauthorized Access"
                    Utils.T(context, displayMessage)
                    Utils.UnAuthorizationToken(requireContext())
                }
            }
            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Package not found. Please try again."
                    Utils.T(requireContext(), displayMessage)
                }
            }
            else -> {
                Utils.T(context, "Unknown error occurred.")
            }
        }
    }



}
