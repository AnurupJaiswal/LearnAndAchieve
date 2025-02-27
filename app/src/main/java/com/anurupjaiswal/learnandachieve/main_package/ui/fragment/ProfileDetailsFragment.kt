package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.User
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.BottomSheetCameraGallaryBinding
import com.anurupjaiswal.learnandachieve.databinding.BottomSheetPermissionSettingsBinding
import com.anurupjaiswal.learnandachieve.databinding.BottomSheetProfileOptionsBinding
import com.anurupjaiswal.learnandachieve.databinding.FragmentProfileDetailsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.DetailsAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.ApiResponse
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileDetailsFragment : Fragment(R.layout.fragment_profile_details) {

    private var _binding: FragmentProfileDetailsBinding? = null
    private val binding get() = _binding!!
    private var apiService: ApiService? = null
    private lateinit var personalDetailsTab: TextView
    private lateinit var contactDetailsTab: TextView
    private lateinit var personalDetails: Map<String, String?>
    private lateinit var contactDetails: Map<String, String?>
    private val TAG = "ProfileDetailsFragment"
    private lateinit var detailsAdapter: DetailsAdapter

    // Variables for temporary file and its URI for the camera image
    private var cameraImageUri: Uri? = null
    private var cameraImageFile: File? = null

    // Launcher for camera intent with FileProvider
    private val cameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                cameraImageFile?.let { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.ivProfile.setImageBitmap(bitmap)
                    // Upload the image file (camera image)
                    uploadProfilePicture(file)
                } ?: run {
                    Log.e(TAG, "Camera image file is null.")
                }
            }
        }

    // Launcher for gallery intent
    private val galleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            try {
                uri?.let { imageUri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        imageUri
                    )
                    binding.ivProfile.setImageBitmap(bitmap)
                    // Upload gallery image by converting bitmap to file
                    uploadProfilePicture(bitmap)
                } ?: run {
                    Log.e(TAG, "Gallery returned null URI.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image from gallery: ${e.message}", e)
            }
        }

    // Launcher for requesting the camera permission
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                showPermissionSettingsBottomSheet("Camera")
            }
        }

    // Launcher for requesting gallery permissions
    private val requestGalleryPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                openGallery()
            } else {
                showPermissionSettingsBottomSheet("Gallery")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileDetailsBinding.bind(view)
        setupUI()
    }

    private fun setupUI() {
        try {
            apiService = RetrofitClient.client
            getUserDetails(Utils.GetSession().token!!)

            Utils.Picasso(
                Utils.GetSession().profilePicture.toString(),
                binding.ivProfile,
                R.drawable.ic_profile
            )
            personalDetailsTab = binding.personalDetailsTab
            contactDetailsTab = binding.contactDetailsTab
            binding.profileDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            detailsAdapter = DetailsAdapter(emptyMap())
            binding.profileDetailsRecyclerView.adapter = detailsAdapter

            val user = Utils.GetSession()

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
            ).filterValues { !it.isNullOrEmpty() && it != "null" }

            contactDetails = mapOf(
                "Email" to user.email,
                "Mobile" to user.mobile?.toString(),
                "Address Line -1" to user.addressLineOne,
                "Address Line -2" to user.addressLineTwo,
                "State" to user.state,
                "District" to user.district,
                "Taluka" to user.taluka
            ).filterValues { !it.isNullOrEmpty() && it != "null" }

            Utils.Picasso(
                Utils.GetSession().profilePicture.toString(),
                binding.ivProfile,
                R.drawable.ic_profile
            )
            Log.d(TAG, "Personal Details: $personalDetails")
            Log.d(TAG, "Contact Details: $contactDetails")
            updateTabUI(isPersonalDetailsSelected = true)

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
                personalDetailsTab.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
                contactDetailsTab.background = null
                contactDetailsTab.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.black)
                )
                detailsAdapter.updateData(personalDetails)
            } else {
                contactDetailsTab.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.toggle_selected)
                contactDetailsTab.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
                personalDetailsTab.background = null
                personalDetailsTab.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.black)
                )
                detailsAdapter.updateData(contactDetails)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateTabUI: ${e.message}", e)
        }
    }

    /**
     * Shows a bottom sheet with options for changing or deleting the profile picture.
     */
    private fun showProfileOptionsBottomSheet() {
        val profileOptionsBottomSheetDialog = BottomSheetDialog(requireContext())
        val profileOptionsView = layoutInflater.inflate(R.layout.bottom_sheet_profile_options, null)
        val bindingSheet = BottomSheetProfileOptionsBinding.bind(profileOptionsView)
        profileOptionsBottomSheetDialog.setContentView(profileOptionsView)

        if (Utils.GetSession().profilePicture == null) {
            bindingSheet.rlDeleteProfile.visibility = View.GONE
        } else {
            bindingSheet.rlDeleteProfile.visibility = View.VISIBLE
        }

        bindingSheet.rlChangeProfile.setOnClickListener {
            profileOptionsBottomSheetDialog.dismiss()
            showCameraGalleryOptionsBottomSheet()
        }

        bindingSheet.rlDeleteProfile.setOnClickListener {
            profileOptionsBottomSheetDialog.dismiss()
            deleteProfileImage()
        }

        profileOptionsBottomSheetDialog.show()
    }

    /**
     * Shows a bottom sheet to choose between camera and gallery.
     */
    private fun showCameraGalleryOptionsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_camera_gallary, null)
        val bindingSheet = BottomSheetCameraGallaryBinding.bind(bottomSheetView)
        bottomSheetDialog.setContentView(bottomSheetView)

        bindingSheet.rlRelative5.setOnClickListener {
            bottomSheetDialog.dismiss()
            handlePermissionsForCamera()
        }

        bindingSheet.rlGallery.setOnClickListener {
            bottomSheetDialog.dismiss()
            handlePermissionsForGallery()
        }

        bottomSheetDialog.show()
    }

    /**
     * Checks for Camera permission; if not granted, requests it or shows a settings bottom sheet.
     */
    private fun handlePermissionsForCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    /**
     * Checks for Gallery permissions; if not granted, requests them or shows a settings bottom sheet.
     */
    private fun handlePermissionsForGallery() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isEmpty()) {
            openGallery()
        } else {
            requestGalleryPermissionsLauncher.launch(notGranted.toTypedArray())
        }
    }

    /**
     * Shows a bottom sheet instructing the user to manually grant a permission from app settings.
     */
    private fun showPermissionSettingsBottomSheet(permissionName: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_permission_settings, null)
        val bindingSettings = BottomSheetPermissionSettingsBinding.bind(view)
        bottomSheetDialog.setContentView(view)

        bindingSettings.tvPermissionMessage.text = "Please grant $permissionName permission manually from settings."
        bindingSettings.btnOpenSettings.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        bottomSheetDialog.show()
    }

    /**
     * Opens the camera by creating a temporary file and passing its URI via FileProvider.
     */
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = createTempImageFile()
        cameraImageFile = imageFile
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        cameraLauncher.launch(cameraIntent)
    }

    /**
     * Opens the gallery to select an image.
     */
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Calls the API to delete the profile image.
     */
    private fun deleteProfileImage() {
        val token = "Bearer ${Utils.GetSession().token}"
        apiService?.deleteProfilePicture(token)?.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                when (response.code()) {
                    StatusCodeConstant.OK -> {
                        binding.ivProfile.setImageResource(R.drawable.ic_profile)
                        Utils.T(requireContext(), "Profile picture deleted")
                        getUserDetails(Utils.GetSession().token!!)
                    }
                    StatusCodeConstant.BAD_REQUEST -> {
                        val message = response.body()?.message ?: "Bad request. Please check your data."
                        Utils.T(requireContext(), message)
                    }
                    StatusCodeConstant.UNAUTHORIZED -> {
                        Utils.UnAuthorizationToken(requireContext())
                    }
                    else -> {
                        Utils.T(requireContext(), "An unexpected error occurred.")
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Utils.T(requireContext(), "Request failed. Please try again.")
            }
        })
    }

    /**
     * Gets user details from the API and updates the local database.
     */
    private fun getUserDetails(authToken: String) {
        val tokenWithBearer = "Bearer $authToken"
        apiService?.getUserDetails(tokenWithBearer)?.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val userModel = response.body()
                        if (userModel?.user != null) {
                            val getUser = userModel.user
                            E("User ID: ${getUser.user_id}")
                            E("Full API Response: ${response.body()}")
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
                            UserDataHelper.instance.insertData(userData)
                            E("User data inserted into local database successfully.")
                        } else {
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
                t.printStackTrace()
                Utils.T(context, t.message ?: "Request failed. Please try again later.")
            }
        })
    }

    /**
     * Handles API errors for getUserDetails().
     */
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
                    val displayMessage = message.message
                    Utils.T(requireContext(), displayMessage)
                }
            }
            else -> {
                Utils.T(context, "Unknown error occurred.")
            }
        }
    }

    /**
     * Uploads a profile picture given as a Bitmap (for gallery images).
     */
    private fun uploadProfilePicture(bitmap: Bitmap) {
        try {
            val file = createImageFile(bitmap)
            uploadProfilePicture(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading profile picture: ${e.message}", e)
        }
    }

    /**
     * Overloaded method to upload a profile picture using a File (for camera images).
     */
    private fun uploadProfilePicture(file: File) {
        try {
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val profilePicturePart =
                MultipartBody.Part.createFormData("profilePicture", file.name, requestBody)
            val authToken = "Bearer ${Utils.GetSession().token}"

            apiService?.updateProfile(authToken, profilePicturePart)
                ?.enqueue(object : Callback<GetUserResponse> {
                    override fun onResponse(
                        call: Call<GetUserResponse>,
                        response: Response<GetUserResponse>
                    ) {
                        when (response.code()) {
                            StatusCodeConstant.OK -> {
                                response.body()?.user?.profilePicture?.let {
                                    Utils.Picasso(it, binding.ivProfile, R.drawable.dummy)
                                    Utils.GetSession().token?.let { token -> getUserDetails(token) }
                                }
                                val message = response.body()?.message ?: "Profile updated successfully!!"
                                Utils.T(requireContext(), message)
                            }
                            StatusCodeConstant.BAD_REQUEST -> {
                                val message = response.body()?.message ?: "Bad request. Please check your data."
                                Utils.T(requireContext(), message)
                            }
                            StatusCodeConstant.UNAUTHORIZED -> {
                                Utils.UnAuthorizationToken(requireContext())
                                val message = response.body()?.message ?: "Unauthorized. Please log in again."
                                Utils.T(requireContext(), message)
                            }
                            StatusCodeConstant.NOT_FOUND -> {
                                val message = response.body()?.message ?: "Profile not found."
                                Utils.T(requireContext(), message)
                            }
                            else -> {
                                val message = response.body()?.message ?: "An unexpected error occurred."
                                Utils.T(requireContext(), message)
                            }
                        }
                    }
                    override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                        t.printStackTrace()
                        Utils.T(requireContext(), t.message)
                    }
                })
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading profile picture: ${e.message}", e)
        }
    }

    /**
     * Creates a File from a Bitmap (used for gallery images).
     */
    private fun createImageFile(bitmap: Bitmap): File {
        val fileName = "profile_${System.currentTimeMillis()}.jpg"
        val storageDir = requireContext().cacheDir
        val file = File(storageDir, fileName)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
        }
        return file
    }

    /**
     * Creates a temporary file for the camera image.
     */
    private fun createTempImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = requireContext().cacheDir
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
