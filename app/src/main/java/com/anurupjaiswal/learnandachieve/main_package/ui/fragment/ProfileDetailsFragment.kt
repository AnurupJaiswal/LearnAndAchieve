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
import com.anurupjaiswal.learnandachieve.databinding.FragmentProfileDetailsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.DetailsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileDetailsFragment : Fragment(R.layout.fragment_profile_details) {

    private var _binding: FragmentProfileDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var personalDetailsTab: TextView
    private lateinit var contactDetailsTab: TextView

    private val TAG = "ProfileDetailsFragment"

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
            // Initialize tabs and RecyclerView
            personalDetailsTab = binding.personalDetailsTab
            contactDetailsTab = binding.contactDetailsTab
            binding.profileDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            val jsonData = """
                {
                  "personalDetails": {
                    "First Name": "Rohit",
                    "Middle Name": "Xyz",
                    "Last Name": "Sharma",
                    "Date Of Birth": "05/02/2000",
                    "Gender": "Male",
                    "School Name": "Shantiniketan School",
                    "Medium": "English",
                    "Class": "10th"
                  },
                  "contactDetails": {
                    "Email": "demo@gmail.com",
                    "Mobile": "9812345678",
                    "Address Line 1": "101, DigiHost Pvt Ltd",
                    "Address Line 2": "102, DigiHost Pvt Ltd",
                    "State": "Maharashtra",
                    "District": "Raigad",
                    "Taluka": "Panvel",
                    "Pin Code": "400077"
                  }
                }
            """.trimIndent()

            val jsonObject = JSONObject(jsonData)
            updateTabUI(isPersonalDetailsSelected = true)
            updateRecyclerView(jsonObject.optJSONObject("personalDetails") ?: JSONObject())

            // Tab click listeners
            personalDetailsTab.setOnClickListener {
                updateTabUI(isPersonalDetailsSelected = true)
                updateRecyclerView(jsonObject.optJSONObject("personalDetails") ?: JSONObject())
            }

            contactDetailsTab.setOnClickListener {
                updateTabUI(isPersonalDetailsSelected = false)
                updateRecyclerView(jsonObject.optJSONObject("contactDetails") ?: JSONObject())
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
            } else {
                contactDetailsTab.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.toggle_selected)
                contactDetailsTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                personalDetailsTab.background = null
                personalDetailsTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateTabUI: ${e.message}", e)
        }
    }

    private fun updateRecyclerView(data: JSONObject) {
        try {
            val adapter = DetailsAdapter(data)
            binding.profileDetailsRecyclerView.adapter = adapter
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateRecyclerView: ${e.message}", e)
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
}
